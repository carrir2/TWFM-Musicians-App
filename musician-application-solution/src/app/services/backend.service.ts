import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(
    private http: HttpClient
  ) { }

  domain: string = "http://troymarketmusicianservice-env.eba-uzipcwm3.us-east-1.elasticbeanstalk.com/";

  getHeaders(): any {
    let token = this.getToken();
    if (token == undefined) {
      return new HttpHeaders({"Token": "", "email": ""});
    } else {
      token = JSON.parse(token);
      let headers: HttpHeaders = new HttpHeaders({"Token": token["token"], "email": token["email"]});
      return headers;
    }
  }

  async checkLogged(): Promise<boolean>{
    // assure that user token has not expired in the backend

    // GET                        api/v1/registration/active/
    // {"error": "none"}          if active
    // {"error": "token expired"} if inactive
    // {"error": "invalid token"} if wrong type
    let headers = this.getHeaders();
    let e: any = await firstValueFrom(this.http.get(this.domain + "api/v1/registration/active/", {headers}));
    if (e['error'] == "none") {
      return true;
    } else {
      return false;
    }
  }

  async post(url : string, body : any) {
    // if (!(await this.checkLogged())) {
    //   this.logout();
    //   location.reload();
    // }
    let headers = this.getHeaders();
    return firstValueFrom(this.http.post(this.domain + url, body, {headers}));
  }

  async get(url : string) {
    // if (!(await this.checkLogged())) {
    //   this.logout();
    //   location.reload();
    // }
    let headers = this.getHeaders();
    return firstValueFrom(this.http.get(this.domain + url, {headers}));
  }

  // Account Logic
  isLogged(): boolean {
    if (this.getToken() == undefined) {
      return false;
    } else {
      return true;
    }
  }


  isAdmin(): boolean {
    let token = this.getToken();
    if (token != undefined && JSON.parse(token)["appUserRole"] == "USER") {
      return false;
    } else {
      return true;
    }
  }

  // Token (cookie):
  // { 
  //   "error": String, 
  //   "id": String, 
  //   "email": String, 
  //   "contactName": String, 
  //   "token": String, 
  //   "appUserRole": String, 
  //   "locked": boolean, 
  //   "enabled": String
  //   }

  getToken(): any {
    if (document.cookie.indexOf("user_login_information") == -1) {
      return undefined;
    }
    const parseCookie = (str: any) =>
                        str
                        .split(';')
                        .map((v:any) => v.split('='))
                        .reduce((acc:any, v:any) => {
                          acc[decodeURIComponent(v[0].trim())] = decodeURIComponent(v[1].trim());
                          return acc;
                        }, {});
    let x = document.cookie;  
    return parseCookie(x)["user_login_information"];
  }

  login(response: any): void {
    document.cookie = `user_login_information=${JSON.stringify(response)}; path=/`;
  }

  logout() {
    document.cookie = "user_login_information=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
  }
}
