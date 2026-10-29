import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Prestamo, PrestamoService } from '../../services/prestamo.service';

type Vista = 'activos' | 'historial';
type Filtro = 'todos' | Prestamo['estado'];
interface Toast { id: number; mensaje: string; tipo: 'ok' | 'error' | 'info'; }
interface ErrorInfo { titulo: string; texto: string; }

@Component({
  imports: [DatePipe],
  selector: 'app-devolusionesprestamos',
  styleUrl: './devolusionesprestamos.css',
  templateUrl: './devolusionesprestamos.html',
})
export class Devolusionesprestamos {
  private service = inject(PrestamoService);

  todas = signal<Prestamo[]>([]);
  cargando = signal(true);
  errorCarga = signal('');
  procesando = signal(false);
  vista = signal<Vista>('activos');
  filtro = signal<Filtro>('todos');
  busqueda = signal('');

  aDevolver = signal<Prestamo | null>(null);
  errorModal = signal<ErrorInfo | null>(null);
  toasts = signal<Toast[]>([]);
  private siguienteToast = 0;

  filtros: { valor: Filtro; texto: string }[] = [
    { valor: 'todos', texto: 'Todos' },
    { valor: 'solicitado', texto: 'Solicitado' },
    { valor: 'activo', texto: 'Activo' },
    { valor: 'devuelto', texto: 'Devuelto' },
    { valor: 'rechazado', texto: 'Rechazado' },
    { valor: 'cancelado', texto: 'Cancelado' }
  ];

  activos = computed(() =>
    this.todas()
      .filter((p) => p.estado === 'activo')
      .sort((a, b) => (a.fechaprestamo ?? '').localeCompare(b.fechaprestamo ?? ''))
  );

  devueltosHoy = computed(() => {
    const hoy = new Date().toLocaleDateString('sv-SE'); // yyyy-MM-dd
    return this.todas().filter((p) => p.estado === 'devuelto' && p.fechadevolucionreal === hoy).length;
  });

  visiblesActivos = computed(() => this.buscar(this.activos()));

  visiblesHistorial = computed(() => {
    const f = this.filtro();
    const base = f === 'todos' ? this.todas() : this.todas().filter((p) => p.estado === f);
    return this.buscar(base);
  });

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.cargando.set(true);
    this.errorCarga.set('');
    this.service.historial().subscribe({
      next: (res) => {
        this.todas.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeError(err));
      }
    });
  }

  private recargar() {
    this.service.historial().subscribe({ next: (res) => this.todas.set(res.data ?? []) });
  }

  confirmarDevolucion() {
    const p = this.aDevolver();
    if (!p) return;
    this.procesando.set(true);
    this.service.devolucion(p.id).subscribe({
      next: () => {
        this.procesando.set(false);
        this.aDevolver.set(null);
        this.toast('Devolución registrada exitosamente. Se sumó 1 copia al inventario.', 'ok');
        this.recargar();
      },
      error: (err) => {
        this.procesando.set(false);
        this.aDevolver.set(null);
        const msg = this.mensajeError(err);
        this.errorModal.set(
          err.status === 409 && /devuelto/i.test(msg)
            ? { titulo: 'Préstamo ya devuelto', texto: 'Este préstamo ya fue devuelto. El inventario ya se encuentra actualizado.' }
            : { titulo: 'No se puede procesar', texto: msg }
        );
        this.recargar();
      }
    });
  }

  // ---------- Auxiliares de la vista ----------
  estadoBadge(estado: Prestamo['estado']): { texto: string; clases: string; punto: string } {
    const mapa = {
      solicitado: { texto: 'Solicitado', clases: 'bg-tertiary-fixed text-on-tertiary-fixed', punto: 'bg-on-tertiary-fixed' },
      activo: { texto: 'Activo', clases: 'bg-surface-container text-secondary', punto: 'bg-secondary' },
      devuelto: { texto: 'Devuelto', clases: 'bg-[#ECFDF5] text-[#047857]', punto: 'bg-[#10B981]' },
      rechazado: { texto: 'Rechazado', clases: 'bg-error-container/40 text-error', punto: 'bg-error' },
      cancelado: { texto: 'Cancelado', clases: 'bg-surface-container text-on-surface-variant', punto: 'bg-outline' }
    };
    return mapa[estado];
  }

  dias(p: Prestamo): string {
    if (p.diasenprestamo == null) return '—';
    return p.diasenprestamo === 1 ? '1 día' : `${p.diasenprestamo} días`;
  }

  iniciales(nombre?: string): string {
    return (nombre ?? '').split(' ').filter(Boolean).slice(0, 2).map((n) => n[0]).join('').toUpperCase();
  }

  toastClases(t: Toast): string {
    return t.tipo === 'ok' ? 'bg-secondary text-on-secondary' : t.tipo === 'error' ? 'bg-error text-on-error' : 'bg-primary text-on-primary';
  }

  private buscar(lista: Prestamo[]): Prestamo[] {
    const q = this.normalizar(this.busqueda());
    if (!q) return lista;
    return lista.filter((p) =>
      this.normalizar(
        [p.estudiante?.nombrecompleto, p.estudiante?.documento, p.libro?.titulo, p.libro?.autor, p.libro?.isbn, p.estado].join(' ')
      ).includes(q)
    );
  }

  private toast(mensaje: string, tipo: Toast['tipo']) {
    const id = ++this.siguienteToast;
    this.toasts.update((l) => [...l, { id, mensaje, tipo }]);
    setTimeout(() => this.toasts.update((l) => l.filter((t) => t.id !== id)), 4000);
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

