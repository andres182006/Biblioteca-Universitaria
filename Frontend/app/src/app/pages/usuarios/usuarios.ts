import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Observable, forkJoin } from 'rxjs';
import { Estudiante, UsuarioCuenta, UsuarioService } from '../../services/usuario.service';


type Tipo = 'estudiante' | 'profesor';
type Tab = 'estudiantes' | 'profesores';
interface Toast { id: number; mensaje: string; tipo: 'ok' | 'error'; }
interface Edicion { tipo: Tipo; id: number; documento: string; }

const porFechaDesc = <T extends { fecharegistro: string | null }>(a: T, b: T) =>
  (b.fecharegistro ?? '').localeCompare(a.fecharegistro ?? '');
@Component({
  imports: [ReactiveFormsModule, DatePipe],
  selector: 'app-usuarios',
  styleUrl: './usuarios.css',
  templateUrl: './usuarios.html',
})
export class Usuarios implements OnInit {
  private service = inject(UsuarioService);
  private fb = inject(FormBuilder);

  estudiantes = signal<Estudiante[]>([]);
  profesores = signal<UsuarioCuenta[]>([]);
  cargando = signal(true);
  errorCarga = signal('');
  tab = signal<Tab>('estudiantes');
  busqueda = signal('');

  // Nuevo usuario
  nuevoAbierto = signal(false);
  tipoNuevo = signal<Tipo>('estudiante');
  verClave = signal(false);
  guardando = signal(false);

  // Edición
  editando = signal<Edicion | null>(null);

  // Detalle, eliminación
  detalle = signal<UsuarioCuenta | null>(null);
  aEliminar = signal<Estudiante | null>(null);
  eliminando = signal(false);
  bloqueado = signal<Estudiante | null>(null);

  toasts = signal<Toast[]>([]);
  private siguienteToast = 0;

  formNuevo = this.fb.nonNullable.group({
    nombrecompleto: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(120)]],
    documento: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(20)]],
    correo: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
    contrasena: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
    telefono: ['', [Validators.maxLength(20)]]
  });

  formEditar = this.fb.nonNullable.group({
    nombrecompleto: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(120)]],
    correo: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
    telefono: ['', [Validators.maxLength(20)]]
  });

  // ---------- Datos derivados ----------
  total = computed(() => this.estudiantes().length + this.profesores().length);

  visiblesEstudiantes = computed(() => this.filtrar(this.estudiantes()));
  visiblesProfesores = computed(() => this.filtrar(this.profesores()));

  private filtrar<T extends { nombrecompleto: string; documento: string; correo: string }>(lista: T[]): T[] {
    const q = this.normalizar(this.busqueda());
    if (!q) return lista;
    return lista.filter((u) =>
      this.normalizar([u.nombrecompleto, u.documento, u.correo].join(' ')).includes(q)
    );
  }

  ngOnInit() {
    this.cargar();
  }

  // ---------- Carga ----------
  cargar() {
    this.cargando.set(true);
    this.errorCarga.set('');
    forkJoin({ est: this.service.listarEstudiantes(), usr: this.service.listarUsuarios() }).subscribe({
      next: ({ est, usr }) => {
        this.aplicar(est.data, usr.data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeError(err));
      }
    });
  }

  private recargar() {
    forkJoin({ est: this.service.listarEstudiantes(), usr: this.service.listarUsuarios() }).subscribe({
      next: ({ est, usr }) => this.aplicar(est.data, usr.data)
    });
  }

  private aplicar(est: Estudiante[] | null, usr: UsuarioCuenta[] | null) {
    this.estudiantes.set([...(est ?? [])].sort(porFechaDesc));
    this.profesores.set((usr ?? []).filter((u) => u.rol === 'profesor').sort(porFechaDesc));
  }

  cambiarTab(t: Tab) {
    this.tab.set(t);
    this.busqueda.set('');
  }

  restablecer() {
    this.busqueda.set('');
    this.recargar();
    this.toast('Datos y filtros restablecidos con éxito', 'ok');
  }

  // ---------- Nuevo usuario ----------
  abrirNuevo() {
    this.formNuevo.reset();
    this.tipoNuevo.set('estudiante');
    this.verClave.set(false);
    this.nuevoAbierto.set(true);
  }

  cerrarNuevo() {
    this.nuevoAbierto.set(false);
  }

  guardarNuevo() {
    if (this.formNuevo.invalid) {
      this.formNuevo.markAllAsTouched();
      return;
    }
    const v = this.formNuevo.getRawValue();
    const base = {
      nombrecompleto: v.nombrecompleto.trim(),
      documento: v.documento.trim(),
      correo: v.correo.trim(),
      contrasena: v.contrasena
    };
    const tipo = this.tipoNuevo();
    this.guardando.set(true);

    const peticion: Observable<unknown> =
      tipo === 'estudiante'
        ? this.service.crearEstudiante({ ...base, telefono: v.telefono.trim() || null })
        : this.service.crearProfesor(base);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cerrarNuevo();
        this.cambiarTab(tipo === 'estudiante' ? 'estudiantes' : 'profesores');
        this.toast(
          `Usuario ${base.nombrecompleto} registrado exitosamente como ${tipo === 'estudiante' ? 'Estudiante' : 'Profesor'}.`,
          'ok'
        );
        this.recargar();
      },
      error: (err) => {
        this.guardando.set(false);
        this.toast(this.mensajeGuardado(err), 'error');
      }
    });
  }

  // ---------- Editar ----------
  abrirEditar(tipo: Tipo, u: { id: number; documento: string; nombrecompleto: string; correo: string; telefono?: string | null }) {
    this.editando.set({ tipo, id: u.id, documento: u.documento });
    this.formEditar.reset({
      nombrecompleto: u.nombrecompleto,
      correo: u.correo,
      telefono: u.telefono ?? ''
    });
  }

  cerrarEditar() {
    this.editando.set(null);
  }

  guardarEditar() {
    const e = this.editando();
    if (!e) return;
    if (this.formEditar.invalid) {
      this.formEditar.markAllAsTouched();
      return;
    }
    const v = this.formEditar.getRawValue();
    const nombre = v.nombrecompleto.trim();
    const correo = v.correo.trim();
    this.guardando.set(true);

    const peticion: Observable<unknown> =
      e.tipo === 'estudiante'
        ? this.service.actualizarEstudiante(e.id, { nombrecompleto: nombre, correo, telefono: v.telefono.trim() || null })
        : this.service.actualizarProfesor(e.id, { nombrecompleto: nombre, correo });

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cerrarEditar();
        this.toast(`Cambios guardados con éxito para ${nombre}.`, 'ok');
        this.recargar();
      },
      error: (err) => {
        this.guardando.set(false);
        this.toast(this.mensajeGuardado(err), 'error');
      }
    });
  }

  // ---------- Eliminar estudiante ----------
  confirmarEliminar() {
    const est = this.aEliminar();
    if (!est) return;
    this.eliminando.set(true);
    this.service.eliminarEstudiante(est.id).subscribe({
      next: () => {
        this.eliminando.set(false);
        this.aEliminar.set(null);
        this.toast(`Estudiante "${est.nombrecompleto}" eliminado correctamente.`, 'ok');
        this.recargar();
      },
      error: (err) => {
        this.eliminando.set(false);
        this.aEliminar.set(null);
        if (err.status === 409) {
          this.bloqueado.set(est);
        } else {
          this.toast(this.mensajeError(err), 'error');
        }
      }
    });
  }

  // ---------- Auxiliares de la vista ----------
  inicial(nombre: string): string {
    return (nombre ?? '').trim().charAt(0).toUpperCase();
  }

  errorDe(c: AbstractControl): string {
    if (!(c.invalid && (c.touched || c.dirty))) return '';
    if (c.hasError('required') || c.hasError('pattern')) return 'Este campo es obligatorio';
    if (c.hasError('email')) return 'Ingresa un correo válido';
    if (c.hasError('minlength')) return 'Debe tener al menos 8 caracteres';
    if (c.hasError('maxlength')) return 'Supera la longitud permitida';
    return '';
  }

  toastClases(t: Toast): string {
    return t.tipo === 'ok' ? 'bg-secondary text-on-secondary' : 'bg-error text-on-error';
  }

  cerrarToast(id: number) {
    this.toasts.update((l) => l.filter((t) => t.id !== id));
  }

  private toast(mensaje: string, tipo: Toast['tipo']) {
    const id = ++this.siguienteToast;
    this.toasts.update((l) => [...l, { id, mensaje, tipo }]);
    setTimeout(() => this.cerrarToast(id), 4000);
  }

  private mensajeGuardado(err: any): string {
    const msg = this.mensajeError(err);
    if (err.status === 409) {
      if (/documento/i.test(msg)) return 'El documento ya está registrado en el sistema';
      if (/correo/i.test(msg)) return 'El correo electrónico ya se encuentra registrado';
    }
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
