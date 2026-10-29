import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Libro, LibroRequest, LibroService } from '../../services/Libro.service';

interface Toast { id: number; mensaje: string; tipo: 'ok' | 'error'; }

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-libro',
  styleUrl: './libro.css',
  templateUrl: './libro.html',
})
export class Libros implements OnInit {
  private service = inject(LibroService);
  private fb = inject(FormBuilder);

  libros = signal<Libro[]>([]);
  cargando = signal(true);
  errorCarga = signal('');
  busqueda = signal('');
  categoria = signal('ALL');

  drawerAbierto = signal(false);
  editando = signal<Libro | null>(null);
  minCopias = signal(1);
  guardando = signal(false);

  aEliminar = signal<Libro | null>(null);
  eliminando = signal(false);
  errorBloqueo = signal('');

  toasts = signal<Toast[]>([]);
  private siguienteToast = 0;

  form = this.fb.nonNullable.group({
    titulo: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(200)]],
    autor: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(150)]],
    isbn: ['', [Validators.maxLength(20)]],
    categoria: ['', [Validators.maxLength(80)]],
    cantidadtotal: [1, [Validators.required, Validators.min(1)]]
  });

  // ---------- Datos derivados ----------
  totalTitulos = computed(() => this.libros().length);
  totalCopias = computed(() => this.libros().reduce((s, l) => s + l.cantidadtotal, 0));
  copiasDisponibles = computed(() => this.libros().reduce((s, l) => s + l.cantidaddisponible, 0));

  categorias = computed(() =>
    Array.from(new Set(this.libros().map((l) => l.categoria).filter((c): c is string => !!c))).sort()
  );

  visibles = computed(() => {
    const q = this.normalizar(this.busqueda());
    const cat = this.categoria();
    return this.libros().filter((l) => {
      const coincideTexto =
        !q || this.normalizar([l.titulo, l.autor, l.isbn ?? ''].join(' ')).includes(q);
      const coincideCategoria = cat === 'ALL' || l.categoria === cat;
      return coincideTexto && coincideCategoria;
    });
  });

  hayFiltros = computed(() => this.busqueda().trim() !== '' || this.categoria() !== 'ALL');

  ngOnInit() {
    this.cargar();
  }

  // ---------- Carga ----------
  cargar() {
    this.cargando.set(true);
    this.errorCarga.set('');
    this.service.listar().subscribe({
      next: (res) => {
        this.aplicarLista(res.data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeError(err));
      }
    });
  }

  private recargar() {
    this.service.listar().subscribe({ next: (res) => this.aplicarLista(res.data) });
  }

  private aplicarLista(datos: Libro[] | null) {
    const lista = [...(datos ?? [])].sort((a, b) => a.titulo.localeCompare(b.titulo));
    this.libros.set(lista);
    if (this.categoria() !== 'ALL' && !this.categorias().includes(this.categoria())) {
      this.categoria.set('ALL');
    }
  }

  resetFiltros() {
    this.busqueda.set('');
    this.categoria.set('ALL');
  }

  // ---------- Crear / editar ----------
  prestadas(l: Libro): number {
    return l.cantidadtotal - l.cantidaddisponible;
  }

  abrirDrawer(libro?: Libro) {
    const minimo = libro ? Math.max(1, this.prestadas(libro)) : 1;
    this.editando.set(libro ?? null);
    this.minCopias.set(minimo);
    this.form.controls.cantidadtotal.setValidators([Validators.required, Validators.min(minimo)]);
    this.form.reset({
      titulo: libro?.titulo ?? '',
      autor: libro?.autor ?? '',
      isbn: libro?.isbn ?? '',
      categoria: libro?.categoria ?? '',
      cantidadtotal: libro?.cantidadtotal ?? 1
    });
    this.drawerAbierto.set(true);
  }

  cerrarDrawer() {
    this.drawerAbierto.set(false);
  }

  invalido(campo: keyof typeof this.form.controls): boolean {
    const c = this.form.controls[campo];
    return c.invalid && (c.touched || c.dirty);
  }

  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const body: LibroRequest = {
      titulo: v.titulo.trim(),
      autor: v.autor.trim(),
      isbn: v.isbn.trim() || null,
      categoria: v.categoria.trim() || null,
      cantidadtotal: v.cantidadtotal
    };
    const edicion = this.editando();
    this.guardando.set(true);

    const peticion = edicion ? this.service.actualizar(edicion.id, body) : this.service.crear(body);
    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cerrarDrawer();
        this.toast(edicion ? 'Libro actualizado exitosamente.' : 'Libro registrado exitosamente en el catálogo.', 'ok');
        this.recargar();
      },
      error: (err) => {
        this.guardando.set(false);
        this.toast(this.mensajeGuardado(err), 'error');
      }
    });
  }

  // ---------- Eliminar ----------
  confirmarEliminar() {
    const l = this.aEliminar();
    if (!l) return;
    this.eliminando.set(true);
    this.service.eliminar(l.id).subscribe({
      next: () => {
        this.eliminando.set(false);
        this.aEliminar.set(null);
        this.toast('Libro eliminado del catálogo.', 'ok');
        this.recargar();
      },
      error: (err) => {
        this.eliminando.set(false);
        this.aEliminar.set(null);
        if (err.status === 409) {
          this.errorBloqueo.set(this.mensajeError(err));
        } else {
          this.toast(this.mensajeError(err), 'error');
        }
      }
    });
  }

  // ---------- Auxiliares ----------
  toastClases(t: Toast): string {
    return t.tipo === 'error' ? 'bg-error-container text-on-error-container' : 'bg-primary-container text-on-primary';
  }

  private toast(mensaje: string, tipo: Toast['tipo']) {
    const id = ++this.siguienteToast;
    this.toasts.update((l) => [...l, { id, mensaje, tipo }]);
    setTimeout(() => this.cerrarToast(id), 4000);
  }

  cerrarToast(id: number) {
    this.toasts.update((l) => l.filter((t) => t.id !== id));
  }

  private mensajeGuardado(err: any): string {
    const msg = this.mensajeError(err);
    if (err.status === 409 && /isbn/i.test(msg)) return 'El ISBN ya está registrado';
    if (err.status === 409 && /t[ií]tulo/i.test(msg)) return 'Ya existe un libro con ese título';
    return msg;
  }

  private mensajeError(err: any): string {
    if (err.status === 0) return 'No se pudo conectar con el servidor. Inténtalo de nuevo.';
    if (err.status === 403) return 'Tu sesión expiró o no tienes permisos para esta acción. Vuelve a iniciar sesión.';
    return err.error?.message ?? 'Ocurrió un error inesperado.';
  }

  private normalizar(s: string): string {
    return (s ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
  }
}

