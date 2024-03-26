import { BackendService } from './services/backend.service';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title: string = 'musician-application-solution';

  constructor(
    private backend: BackendService
  ) { }

  ngOnInit(): void {
    if (this.backend.isLogged()) {
      if (this.backend.isAdmin()) {
        this.currentPage = "Calendar";
      } else {
        this.currentPage = "Apply";
      }
    } else {
      this.currentPage = "Login";
    }
  }

  currentPage: string = "Login";
  changePage(page: string) {
    this.updateHeader();
    this.currentPage = page;
  }

  headUpdater: number = 0;
  updateHeader(): void {
    if (this.headUpdater) {
      this.headUpdater = 0;
    } else {
      this.headUpdater = 1;
    }
  }
}
