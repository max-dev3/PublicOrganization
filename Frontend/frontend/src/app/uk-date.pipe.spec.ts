import { UkDatePipe } from './uk-date.pipe';

describe('UkDatePipe', () => {
  it('create an instance', () => {
    const pipe = new UkDatePipe('en-US');
    expect(pipe).toBeTruthy();
  });
});
