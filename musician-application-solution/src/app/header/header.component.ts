import { BackendService } from './../services/backend.service';
import { ThisReceiver } from '@angular/compiler';
import { Component, OnInit, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { application } from 'express';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(
    private backend: BackendService
  ) { }

  ngOnInit(): void {
    this.updateMenu();
  }

  @Input() update: number = 0;
  @Output() updateParent = new EventEmitter<any>();
  ngOnChanges(changes: SimpleChanges) {
    if (changes['update'].previousValue == 0 && changes['update'].currentValue == 1) {
      this.updateMenu();
      this.updateParent.emit();
    }
  }

  // navigation
  logged: string = "Login";
  menuItems: string[] = ["Apply", this.logged, "Calendar"];
  @Output() navEmitter = new EventEmitter();
  goto(item: string) {
    if (item == "Logout") {
      confirm("Are you sure you want to log out?");
      let formJSON: any = {};
      this.backend.post("api/v1/registration/logout/", formJSON)
      this.backend.logout();
      this.navEmitter.emit("Login");
    }
    this.updateMenu();
    if (this.menuItems.includes(item)) {
      this.navEmitter.emit(item);
    }
  }

  updateLogged(): boolean {
    if (this.backend.isLogged()) {
      this.logged = "Logout";
      return true;
    } else {
      this.logged = "Login";
      return false
    }
  }

  updateMenu(): void {
    if (this.updateLogged()) {
      if (JSON.parse(this.backend.getToken())["appUserRole"] == "USER") { // user
        this.menuItems = ["Apply", this.logged];
      } else { // admin
        this.menuItems = [this.logged, "Calendar"];
      }
    } else {
      this.menuItems = [this.logged];
    }
  }
}
