import {postData} from './utils/commonUtils.js'

let form = document.querySelector("form");
let commonAlert = form.querySelector("#common-error");
let nameAlert = form.querySelector(".signup-name .text-warning");
let mailAlert = form.querySelector(".signup-email .text-warning");
let phoneAlert = form.querySelector(".signup-phone .text-warning");
let passwordAlert = form.querySelector(".signup-password .text-warning");
let message;
let data = {};
let url = "/api/v1/user/signup"
let signUp = () => {
    form.addEventListener("submit", (e) => {
        e.preventDefault();
        data = {
            name: form.name.value,
            phoneNumber: form.phoneNumber.value,
            password: form.password.value,
            email: form.email.value,
        }
        postData(url, data).then(response => {
            console.log(response)
            if (response.message) {
                message = response.message;
                if (message.email) mailAlert.innerText = message.email;
                if (message.error) commonAlert.innerText = message.error;
                if (message.success) {
                    alert(message.success)
                    location.href = '/user/login';
                }
            }
        })
    })
}

signUp()
