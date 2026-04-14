import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface Building {
  id: number;
  name: string;
  street: string;
  city: string;
  zipCode: string;
  country: string;
}

@Injectable({
  providedIn: 'root'
})


export class ApiService {
  private readonly apiUrl = '/api';
  constructor( private httpClient: HttpClient) { }

getBuildings() {
  return this.httpClient.get<Building[]>(`${this.apiUrl}/buildings`);
}

addBuilding(building: Omit<Building, 'id'>) {
  return this.httpClient.post<Building>(`${this.apiUrl}/buildings`, building);
}

modifyBuilding(id: number, building: Omit<Building, 'id'>) {
  return this.httpClient.put<Building>(`${this.apiUrl}/buildings/${id}`, building);   
}

deleteBuilding(id: number) {
  return this.httpClient.delete(`${this.apiUrl}/buildings/${id}`);
}

}


export default ApiService;