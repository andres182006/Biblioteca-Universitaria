import { Component, OnInit, computed, inject, signal } from '@angular/core';
import{DatePipe} from '@angular/common';
import{FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Observable, forkJoin} from 'rxjs';
import{EstadoReserva, Reserva, ReservaService} from '../../services/reserva.service';
import{Videobeam, VideobeamService} from '../../services/videobeam.service';

type Tab = 'todas' | EstadoReserva;
type Accion = 'entregar' | 'devolver' | 'cancelar';
interface Toast{id: number; mensaje: string; tipo: 'ok' | 'error';}
@Component({
  imports: [ReactiveFormsModule, DatePipe],
  standalone: true,
  selector: 'app-reservas',
  styleUrl: './reservas.css',
  templateUrl: './reservas.html',
})
export class Reservas implements OnInit {
  private service = inject(ReservaService);
  private videobeamService = inject(VideobeamService);
  private fb = inject(FormBuilder);

  reservas = signal<Reserva[]>([]);
  videobeams = signal<Videobeam[]>([]);
  cargando = signal(true);
  errorCarga = signal('');
  procesando = signal(false);

  tab = signal<Tab>('todas');
  busqueda = signal('');
  fechaFiltro = signal('');
  videobeamFiltro = signal('todos');

  confirmar = signal<{ tipo: Accion; reserva: Reserva } | null>(null);
  editando = signal<Reserva | null>(null);
  errorForm = signal('');

  toasts = signal<Toast[]>([]);
  private siguienteToast = 0;

  hoy = new Date().toLocaleDateString('sv-SE'); // yyyy-MM-dd

  tabs: { valor: Tab; texto: string }[] = [
    { valor: 'todas', texto: 'Todas' },
    { valor: 'pendiente', texto: 'Pendientes' },
    { valor: 'entregado', texto: 'Entregadas' },
    { valor: 'devuelto', texto: 'Devueltas' },
    { valor: 'cancelado', texto: 'Canceladas' }
  ];

  form = this.fb.nonNullable.group({
    fechareserva: ['', [Validators.required]],
    horainicio: ['', [Validators.required]],
    horafin: ['', [Validators.required]],
    idvideobeam: [0, [Validators.min(1)]],
    motivo: ['', [Validators.maxLength(200)]]
  });

  // ---------- Datos derivados ----------
  conteos = computed(() => {
    const c = { todas: 0, pendiente: 0, entregado: 0, devuelto: 0, cancelado: 0 };
    for (const r of this.reservas()) {
      c.todas++;
      c[r.estado]++;
    }
    return c;
  });

  visibles = computed(() => {
    const tab = this.tab();
    const q = this.normalizar(this.busqueda());
    const fecha = this.fechaFiltro();
    const vb = this.videobeamFiltro();
    return this.reservas().filter((r) => {
      const coincideTab = tab === 'todas' || r.estado === tab;
      const coincideTexto =
        !q || this.normalizar(`${r.usuarioprofesor?.nombrecompleto} ${r.usuarioprofesor?.documento}`).includes(q);
      const coincideFecha = !fecha || r.fechareserva === fecha;
      const coincideVb = vb === 'todos' || String(r.videobeam?.id) === vb;
      return coincideTab && coincideTexto && coincideFecha && coincideVb;
    });
  });

  hayFiltros = computed(
    () => this.tab() !== 'todas' || this.busqueda().trim() !== '' || this.fechaFiltro() !== '' || this.videobeamFiltro() !== 'todos'
  );

  ngOnInit() {
    this.cargar();
  }

  // ---------- Carga ----------
  cargar() {
    this.cargando.set(true);
    this.errorCarga.set('');
    forkJoin({ res: this.service.listar(), vb: this.videobeamService.listar() }).subscribe({
      next: ({ res, vb }) => {
        this.aplicar(res.data, vb.data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeError(err));
      }
    });
  }

  private recargar() {
    forkJoin({ res: this.service.listar(), vb: this.videobeamService.listar() }).subscribe({
      next: ({ res, vb }) => this.aplicar(res.data, vb.data)
    });
  }

  private aplicar(res: Reserva[] | null, vb: Videobeam[] | null) {
    this.reservas.set(res ?? []);
    this.videobeams.set(
      [...(vb ?? [])].sort((a, b) => a.codigo.localeCompare(b.codigo, undefined, { numeric: true }))
    );
  }

  restablecerFiltros() {
    this.tab.set('todas');
    this.busqueda.set('');
    this.fechaFiltro.set('');
    this.videobeamFiltro.set('todos');
  }

  // ---------- Entregar / devolver / cancelar ----------
  meta(tipo: Accion) {
    switch (tipo) {
      case 'entregar':
        return {
          titulo: 'Confirmar entrega de videobeam', icono: 'verified',
          iconoClases: 'bg-surface-container-high text-primary-container',
          boton: 'Confirmar entrega', botonClases: 'bg-primary-container hover:bg-secondary',
          cancelar: 'Cancelar', exito: 'Entrega registrada'
        };
      case 'devolver':
        return {
          titulo: 'Confirmar devolución', icono: 'assignment_turned_in',
          iconoClases: 'bg-secondary-fixed text-secondary',
          boton: 'Confirmar devolución', botonClases: 'bg-secondary hover:bg-primary-container',
          cancelar: 'Cancelar', exito: 'Devolución registrada'
        };
      default:
        return {
          titulo: 'Cancelar reserva', icono: 'cancel',
          iconoClases: 'bg-error-container text-error',
          boton: 'Confirmar cancelación', botonClases: 'bg-error hover:bg-on-error-container',
          cancelar: 'Volver', exito: 'Reserva cancelada'
        };
    }
  }

  textoConfirmacion(c: { tipo: Accion; reserva: Reserva }): string {
    const r = c.reserva;
    if (c.tipo === 'entregar') return `¿Registrar la entrega del ${r.videobeam.codigo} a ${r.usuarioprofesor.nombrecompleto}?`;
    if (c.tipo === 'devolver') return `¿Registrar la devolución del videobeam ${r.videobeam.codigo}?`;
    return `¿Cancelar la reserva #${r.id}? El horario quedará libre.`;
  }

  ejecutar() {
    const c = this.confirmar();
    if (!c) return;
    const id = c.reserva.id;
    this.procesando.set(true);

    const peticion: Observable<unknown> =
      c.tipo === 'entregar' ? this.service.entregar(id)
      : c.tipo === 'devolver' ? this.service.devolver(id)
      : this.service.cancelar(id);

    peticion.subscribe({
      next: () => {
        this.procesando.set(false);
        this.confirmar.set(null);
        this.toast(this.meta(c.tipo).exito, 'ok');
        this.recargar();
      },
      error: (err) => {
        this.procesando.set(false);
        this.confirmar.set(null);
        this.toast(this.mensajeError(err), 'error');
        this.recargar();
      }
    });
  }

  // ---------- Modificar ----------
  abrirModificar(r: Reserva) {
    this.errorForm.set('');
    this.form.reset({
      fechareserva: r.fechareserva,
      horainicio: r.horainicio.slice(0, 5),
      horafin: r.horafin.slice(0, 5),
      idvideobeam: r.videobeam.id,
      motivo: r.motivo ?? ''
    });
    this.editando.set(r);
  }

  cerrarModificar() {
    this.editando.set(null);
  }

  guardarModificacion() {
    const r = this.editando();
    if (!r) return;
    this.errorForm.set('');
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorForm.set('Completa la fecha, el horario y el videobeam.');
      return;
    }
    const v = this.form.getRawValue();
    if (v.fechareserva < this.hoy) {
      this.errorForm.set('La fecha no puede ser pasada');
      return;
    }
    if (v.horainicio >= v.horafin) {
      this.errorForm.set('La hora de inicio debe ser anterior a la hora de fin');
      return;
    }

    this.procesando.set(true);
    this.service
      .actualizar(r.id, {
        idvideobeam: v.idvideobeam,
        fechareserva: v.fechareserva,
        horainicio: v.horainicio,
        horafin: v.horafin,
        motivo: v.motivo.trim() || null
      })
      .subscribe({
        next: () => {
          this.procesando.set(false);
          this.cerrarModificar();
          this.toast('Reserva modificada', 'ok');
          this.recargar();
        },
        error: (err) => {
          this.procesando.set(false);
          this.errorForm.set(this.mensajeError(err)); // mantenimiento, horario ocupado, etc.
        }
      });
  }

  // ---------- Auxiliares de la vista ----------
  estilo(estado: EstadoReserva) {
    const mapa = {
      pendiente: { texto: 'Pendiente', tarjeta: 'border-l-on-tertiary-container', titulo: 'text-primary-container', badge: 'bg-tertiary-fixed text-on-tertiary-fixed-variant', punto: 'bg-on-tertiary-container' },
      entregado: { texto: 'Entregado', tarjeta: 'border-l-secondary', titulo: 'text-primary-container', badge: 'bg-secondary-fixed text-on-secondary-fixed-variant', punto: 'bg-secondary' },
      devuelto: { texto: 'Devuelto', tarjeta: 'border-l-outline opacity-90', titulo: 'text-on-surface-variant', badge: 'bg-surface-container text-on-surface-variant', punto: 'bg-outline' },
      cancelado: { texto: 'Cancelado', tarjeta: 'border-l-error opacity-75', titulo: 'text-error', badge: 'bg-error-container text-on-error-container', punto: 'bg-error' }
    };
    return mapa[estado];
  }

  hora(h: string): string {
    return (h ?? '').slice(0, 5);
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
    setTimeout(() => this.cerrarToast(id), 3200);
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

