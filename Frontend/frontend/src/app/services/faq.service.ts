import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {FAQ} from "./post.service";

@Injectable({
  providedIn: 'root'
})
export class FAQService {

  private apiUrl = 'http://localhost:8080/api/v1/faqs'; // Replace with your API endpoint

  constructor(private http: HttpClient) { }

  getFAQs(): Observable<FAQ[]> {
    return this.http.get<FAQ[]>(this.apiUrl);
  }

  getFAQById(id: number): Observable<FAQ> {
    return this.http.get<FAQ>(`${this.apiUrl}/${id}`);
  }

  addFAQ(faq: FAQ): Observable<FAQ> {
    return this.http.post<FAQ>(this.apiUrl, faq);
  }

  updateFAQ(faq: FAQ): Observable<FAQ> {
    return this.http.put<FAQ>(`${this.apiUrl}/${faq.id}`, faq);
  }

  deleteFAQ(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
