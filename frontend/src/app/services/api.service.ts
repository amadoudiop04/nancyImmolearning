import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// ─── Interfaces ───────────────────────────────────────────────────────────────

export interface Building {
  id: number; name: string; street: string; city: string; zipCode: string; country: string;
}
export interface Landlord {
  id: number; firstName: string; lastName: string; email: string; phone: number;
  street: string; city: string; zipCode: string; country: string;
}
export interface Property {
  id: number; name: string; size: string; kind: string; location: string;
  description?: string; rent?: number; buildingId?: number; landlordId?: number;
}
export interface PropertyDetails {
  id: number; name: string; size: string; kind: string; location: string; description?: string; rent?: number;
  building?: { id: number; name: string; street: string; city: string; zipCode: string; country: string };
  landlord?: { id: number; firstName: string; lastName: string; email: string; phone: number };
  lease?: { id: number; startDate: string; endDate: string; rentAmount: number; currency: string };
  tenant?: { id: number; firstName: string; lastName: string; email: string; phone: number };
}
export interface Tenant {
  id: number; firstName: string; lastName: string; email: string; phone: number;
  street: string; city: string; zipCode: string; country: string;
}
export interface Lease {
  id: number; signatureDate?: string; startDate: string; endDate: string;
  rentAmount: number; currency: string; propertyId?: number; tenantId?: number;
}
export interface Payment {
  id: number; leaseId: number; period: string; amount: number;
  status: string; paidDate?: string; tenantName?: string; propertyName?: string;
}
export interface PaymentStats {
  totalPaid: number; totalPending: number; totalLate: number;
}
export interface Document {
  id: number; name: string; docType: string; createdAt: string;
  propertyId?: number; tenantId?: number;
}
export interface DashboardStats {
  totalProperties: number; activeTenants: number; monthlyRevenue: number; occupancyRate: number;
}

// ─── Service ──────────────────────────────────────────────────────────────────

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly api = '/api';

  constructor(private http: HttpClient) {}

  // Buildings
  getBuildings(): Observable<Building[]> { return this.http.get<Building[]>(`${this.api}/buildings`); }
  addBuilding(b: Omit<Building, 'id'>): Observable<Building> { return this.http.post<Building>(`${this.api}/buildings`, b); }
  modifyBuilding(id: number, b: Omit<Building, 'id'>): Observable<Building> { return this.http.put<Building>(`${this.api}/buildings/${id}`, b); }
  deleteBuilding(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/buildings/${id}`); }

  // Properties
  getProperties(): Observable<Property[]> { return this.http.get<Property[]>(`${this.api}/properties`); }
  getPropertyDetails(): Observable<PropertyDetails[]> { return this.http.get<PropertyDetails[]>(`${this.api}/properties/details`); }
  getPropertyDetail(id: number): Observable<PropertyDetails> { return this.http.get<PropertyDetails>(`${this.api}/properties/${id}/details`); }
  getAvailableProperties(): Observable<PropertyDetails[]> { return this.http.get<PropertyDetails[]>(`${this.api}/properties/available`); }
  createProperty(p: Omit<Property, 'id'>): Observable<Property> { return this.http.post<Property>(`${this.api}/properties`, p); }
  updateProperty(id: number, p: Partial<Property>): Observable<Property> { return this.http.put<Property>(`${this.api}/properties/${id}`, p); }
  deleteProperty(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/properties/${id}`); }

  // Tenants
  getTenants(): Observable<Tenant[]> { return this.http.get<Tenant[]>(`${this.api}/tenants`); }
  getTenant(id: number): Observable<Tenant> { return this.http.get<Tenant>(`${this.api}/tenants/${id}`); }
  createTenant(t: Omit<Tenant, 'id'>): Observable<Tenant> { return this.http.post<Tenant>(`${this.api}/tenants`, t); }
  updateTenant(id: number, t: Partial<Tenant>): Observable<Tenant> { return this.http.put<Tenant>(`${this.api}/tenants/${id}`, t); }
  deleteTenant(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/tenants/${id}`); }

  // Landlords
  getLandlords(): Observable<Landlord[]> { return this.http.get<Landlord[]>(`${this.api}/landlords`); }
  getLandlord(id: number): Observable<Landlord> { return this.http.get<Landlord>(`${this.api}/landlords/${id}`); }
  createLandlord(l: Omit<Landlord, 'id'>): Observable<Landlord> { return this.http.post<Landlord>(`${this.api}/landlords`, l); }
  updateLandlord(id: number, l: Partial<Landlord>): Observable<Landlord> { return this.http.put<Landlord>(`${this.api}/landlords/${id}`, l); }
  deleteLandlord(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/landlords/${id}`); }

  // Leases
  getLeases(): Observable<Lease[]> { return this.http.get<Lease[]>(`${this.api}/leases`); }
  getLease(id: number): Observable<Lease> { return this.http.get<Lease>(`${this.api}/leases/${id}`); }
  createLease(l: Omit<Lease, 'id'>): Observable<Lease> { return this.http.post<Lease>(`${this.api}/leases`, l); }
  updateLease(id: number, l: Partial<Lease>): Observable<Lease> { return this.http.put<Lease>(`${this.api}/leases/${id}`, l); }
  deleteLease(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/leases/${id}`); }

  // Payments
  getPayments(params?: { leaseId?: number; status?: string }): Observable<Payment[]> {
    let p = new HttpParams();
    if (params?.leaseId) p = p.set('leaseId', params.leaseId);
    if (params?.status) p = p.set('status', params.status);
    return this.http.get<Payment[]>(`${this.api}/payments`, { params: p });
  }
  getPaymentStats(): Observable<PaymentStats> { return this.http.get<PaymentStats>(`${this.api}/payments/stats`); }
  createPayment(pay: Omit<Payment, 'id'>): Observable<Payment> { return this.http.post<Payment>(`${this.api}/payments`, pay); }
  updatePayment(id: number, pay: Partial<Payment>): Observable<Payment> { return this.http.put<Payment>(`${this.api}/payments/${id}`, pay); }
  deletePayment(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/payments/${id}`); }

  // Documents
  getDocuments(params?: { propertyId?: number; tenantId?: number }): Observable<Document[]> {
    let p = new HttpParams();
    if (params?.propertyId) p = p.set('propertyId', params.propertyId);
    if (params?.tenantId) p = p.set('tenantId', params.tenantId);
    return this.http.get<Document[]>(`${this.api}/documents`, { params: p });
  }
  createDocument(d: Omit<Document, 'id'>): Observable<Document> { return this.http.post<Document>(`${this.api}/documents`, d); }
  deleteDocument(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/documents/${id}`); }
  generateQuittances(): Observable<Document[]> { return this.http.post<Document[]>(`${this.api}/documents/generate-quittances`, {}); }

  // Dashboard
  getDashboardStats(): Observable<DashboardStats> { return this.http.get<DashboardStats>(`${this.api}/dashboard`); }
}

export default ApiService;
