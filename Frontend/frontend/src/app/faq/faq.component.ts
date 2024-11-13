import { Component, OnInit } from '@angular/core';
import {FAQ} from "../services/post.service";
import {FAQService} from "../services/faq.service";
import {AuthService} from "../services/auth.service";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.css']
})
export class FaqComponent implements OnInit {
  faqs: FAQ[] = [];
  currentUserRole: string = this.userService.getUser()?.role;
  editFAQId: number | null = null;
  showAddFAQForm: boolean = false;
  newFAQ: { question: string; answer: string } = {
    question: '',
    answer: ''
  };
  constructor(private faqService: FAQService, private userService: AuthService) { }

  ngOnInit(): void {
    this.getFAQs();
  }

  isAdminOrModerator(): boolean{
    return this.currentUserRole === 'ADMIN';
  }

  getFAQs(): void {
    this.faqService.getFAQs().subscribe(faqs => {
      this.faqs = faqs.map(faq => ({
        ...faq,
        isOpen: false
      }));
    });
  }

  addFAQ(form: NgForm): void {
    if (form.valid) {
      this.faqService.addFAQ({ question: this.newFAQ.question, answer: this.newFAQ.answer, user: this.userService.getUser() } as FAQ)
        .subscribe(faq => {
          this.faqs.push(faq);
          this.showAddFAQForm = false; // hide the form after FAQ is added
          form.reset();
        });
    }
  }
  cancelAddFAQ(): void {
    this.showAddFAQForm = false;
    this.newFAQ = { question: '', answer: '' };
  }

  deleteFAQ(faq: FAQ): void {
    this.faqs = this.faqs.filter(h => h !== faq);
    this.faqService.deleteFAQ(faq.id).subscribe();
  }

  editFAQ(faq: FAQ): void {
    this.editFAQId = faq.id;
  }

  updateFAQ(form: NgForm, faq: FAQ): void {
    if (form.valid) {
      const updatedFAQ: FAQ = {
        ...faq,
        question: form.value.question,
        answer: form.value.answer
      };
      this.faqService.updateFAQ(updatedFAQ).subscribe(() => {
        this.getFAQs();
        this.editFAQId = null;
      });
    }
  }

  cancelEdit(): void {
    this.editFAQId = null;
  }
}
