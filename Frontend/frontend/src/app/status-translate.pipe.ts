import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statusTranslate'
})
export class StatusTranslatePipe implements PipeTransform {

  transform(value: string): string {
    switch (value) {
      case 'PENDING_APPROVAL':
        return 'Очікує підтвердження';
      case 'REJECTED':
        return 'Відхилено';
      case 'APPROVED':
        return 'Прийнято';
      default:
        return value;
    }
  }
}
