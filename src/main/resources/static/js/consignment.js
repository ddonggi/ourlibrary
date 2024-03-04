import {postData} from './utils/commonUtils.js'

let form = document.querySelector("form");
let url = '/api/v1/book/consignment';
let data={};
let consignment = () => {
    form.addEventListener("submit", (e) => {
        e.preventDefault();
        data = {
            bookName: form.bookName.value,
            ISBN: form.ISBN.value,
            rentPrice: form.rentPrice.value
        }
        postData(url, data).then(response => {
            console.log(response)
            if (response.success) {
                alert(response.success)
                location.href = '/book/consignment';
            }
        })
    })
}
consignment()
