import { Component, OnInit, computed, inject, signal } from '@angular/core';
import{AbstractControl, FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import{EstadoVideobeam, Videobeam, VideobeamService} from '../../services/videobeam.service';

type Filtro = 'todos' | EstadoVideobeam;
interface Toast{id: number; mensaje: string; tipo: 'ok' | 'error';}

@Component({
  imports: [ReactiveFormsModule],
  standalone: true,
  selector: 'app-videobeams',
  styleUrl: './videobeams.css',
  templateUrl: './videobeams.html',
})
export class Videobeams implements OnInit{
   private service = inject(VideobeamService);
  private fb = inject(FormBuilder);

  videobeams = signal<Videobeam[]>([]);
  cargando = signal(true);
  errorCarga = signal('');
  busqueda = signal('');
  filtroEstado = signal<Filtro>('todos');

  formAbierto = signal(false);
  editando = signal<Videobeam | null>(null);
  errorForm = signal('');
  guardando = signal(false);

  aEliminar = signal<Videobeam | null>(null);
  eliminando = signal(false);
  eliminacionBloqueada = signal(false);

  toasts = signal<Toast[]>([]);
  private siguienteToast = 0;

  estados: { valor: EstadoVideobeam; texto: string }[] = [
    { valor: 'disponible', texto: 'Disponible' },
    { valor: 'reservado', texto: 'Reservado' },
    { valor: 'mantenimiento', texto: 'Mantenimiento' }
  ];

  form = this.fb.nonNullable.group({
    codigo: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(20)]],
    descripcion: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(200)]],
    estado: ['disponible' as EstadoVideobeam, [Validators.required]]
  });

  // ---------- Datos derivados ----------
  disponibles = computed(() => this.contar('disponible'));
  reservados = computed(() => this.contar('reservado'));
  enMantenimiento = computed(() => this.contar('mantenimiento'));

  visibles = computed(() => {
    const q = this.normalizar(this.busqueda());
    const estado = this.filtroEstado();
    return this.videobeams().filter((v) => {
      const coincideTexto = !q || this.normalizar(`${v.codigo} ${v.descripcion ?? ''}`).includes(q);
      const coincideEstado = estado === 'todos' || v.estado === estado;
      return coincideTexto && coincideEstado;
    });
  });

  hayFiltros = computed(() => this.busqueda().trim() !== '' || this.filtroEstado() !== 'todos');

  private contar(estado: EstadoVideobeam): number {
    return this.videobeams().filter((v) => v.estado === estado).length;
  }

  ngOnInit() {
    this.cargar();
  }

  // ---------- Carga ----------
  cargar() {
    this.cargando.set(true);
    this.errorCarga.set('');
    this.service.listar().subscribe({
      next: (res) => {
        this.aplicar(res.data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeError(err));
      }
    });
  }

  private recargar() {
    this.service.listar().subscribe({ next: (res) => this.aplicar(res.data) });
  }

  private aplicar(datos: Videobeam[] | null) {
    const lista = [...(datos ?? [])].sort((a, b) =>
      a.codigo.localeCompare(b.codigo, undefined, { numeric: true })
    );
    this.videobeams.set(lista);
  }

  limpiarFiltros() {
    this.busqueda.set('');
    this.filtroEstado.set('todos');
  }

  // ---------- Registrar / editar ----------
  abrirForm(v?: Videobeam) {
    this.editando.set(v ?? null);
    this.errorForm.set('');
    this.form.reset({
      codigo: v?.codigo ?? '',
      descripcion: v?.descripcion ?? '',
      estado: v?.estado ?? 'disponible'
    });
    this.formAbierto.set(true);
  }

  cerrarForm() {
    this.formAbierto.set(false);
  }

  guardar() {
    this.errorForm.set('');
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const edicion = this.editando();
    const codigo = v.codigo.trim();

    // El backend no valida el código repetido al editar, así que se revisa aquí también
    const repetido = this.videobeams().some(
      (x) => x.codigo.toLowerCase() === codigo.toLowerCase() && x.id !== edicion?.id
    );
    if (repetido) {
      this.errorForm.set('Ya existe un videobeam con ese código');
      return;
    }

    const body = { codigo, descripcion: v.descripcion.trim() || null, estado: v.estado };
    this.guardando.set(true);

    const peticion: Observable<unknown> = edicion
      ? this.service.actualizar(edicion.id, body)
      : this.service.crear(body);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cerrarForm();
        this.toast(edicion ? 'Cambios guardados' : 'Videobeam registrado', 'ok');
        this.recargar();
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorForm.set(
          err.status === 409 ? 'Ya existe un videobeam con ese código' : this.mensajeError(err)
        );
      }
    });
  }

  // ---------- Eliminar ----------
  confirmarEliminar() {
    const v = this.aEliminar();
    if (!v) return;
    this.eliminando.set(true);
    this.service.eliminar(v.id).subscribe({
      next: () => {
        this.eliminando.set(false);
        this.aEliminar.set(null);
        this.toast('Videobeam eliminado', 'ok');
        this.recargar();
      },
      error: (err) => {
        this.eliminando.set(false);
        this.aEliminar.set(null);
        if (err.status === 409) {
          this.eliminacionBloqueada.set(true);
        } else {
          this.toast(this.mensajeError(err), 'error');
        }
      }
    });
  }

  // ---------- Auxiliares de la vista ----------
  badge(estado: EstadoVideobeam): { texto: string; clases: string; punto: string } {
    const mapa = {
      disponible: { texto: 'Disponible', clases: 'bg-[#ECFDF5] text-[#047857]', punto: 'bg-[#10B981]' },
      reservado: { texto: 'Reservado', clases: 'bg-secondary-fixed text-on-secondary-fixed-variant', punto: 'bg-secondary' },
      mantenimiento: { texto: 'Mantenimiento', clases: 'bg-tertiary-fixed text-on-tertiary-fixed-variant', punto: 'bg-on-tertiary-container' }
    };
    return mapa[estado];
  }

  errorDe(c: AbstractControl): string {
    if (!(c.invalid && (c.touched || c.dirty))) return '';
    if (c.hasError('maxlength')) return 'Supera la longitud permitida';
    return 'Este campo es obligatorio';
  }

  toastClases(t: Toast): string {
    return t.tipo === 'ok' ? 'bg-primary-container text-on-primary' : 'bg-error text-on-error';
  }

  cerrarToast(id: number) {
    this.toasts.update((l) => l.filter((t) => t.id !== id));
  }

  private toast(mensaje: string, tipo: Toast['tipo']) {
    const id = ++this.siguienteToast;
    this.toasts.update((l) => [...l, { id, mensaje, tipo }]);
    setTimeout(() => this.cerrarToast(id), 3000);
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

