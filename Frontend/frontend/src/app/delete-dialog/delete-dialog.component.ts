import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {Component, EventEmitter, Inject, Output} from '@angular/core';

@Component({
  selector: 'app-delete-dialog',
  templateUrl: 'delete-dialog.component.html',
  styleUrls: ['delete-dialog.component.css']
})
export class DeleteDialogComponent {
  @Output() confirm = new EventEmitter();
  @Output() cancel = new EventEmitter();

  confirmDelete() {
    this.confirm.emit();
  }

  cancelDelete() {
    this.cancel.emit();
  }
}
