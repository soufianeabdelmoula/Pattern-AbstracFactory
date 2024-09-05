import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginHttpService} from "../../shared/services/login-http.service";

@Component({
  selector: 'app-authentification',
  templateUrl: './authentification.component.html',
  styleUrls: ['./authentification.component.css']
})
export class AuthentificationComponent implements OnInit {
  isVisiblePassword: boolean = false;

  loginFormGroup!: FormGroup


  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private loginHttpService: LoginHttpService) {
  }


  ngOnInit(): void {

    if (this.loginHttpService.isLoggedIn()) {
      this.router.navigate(['/tache/habilitation']);

    } else {
      this.isVisiblePassword = false;
      this.authFormInit()
    }

  }

  authFormInit() {
    this.loginFormGroup = this.formBuilder.group({
      login: this.formBuilder.control("", Validators.required),
      motDePasse: this.formBuilder.control("", Validators.required)
    })
  }

  authentification() {

    // Logique pour traiter les données du formulaire
    let userName: string = this.loginFormGroup.value.login;
    let password: string = this.loginFormGroup.value.motDePasse;

    if (this.loginHttpService.isLoggedIn()) {
      console.log("Vous êtes déjà authentifié")
    } else {
      this.loginHttpService.loginActeur(userName, password).subscribe({
        next: data => {
          this.loginHttpService.loadProfile(data)
          this.router.navigateByUrl('/tache/habilitation')
        }, error: err => {
          console.log(err)
        }
      })
    }
  }


}
