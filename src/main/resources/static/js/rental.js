import {getData, postData} from './utils/commonUtils.js'

let bookCategories = document.querySelectorAll("input[type=radio]");
let category; //0 = 대여 많은순, 1= 낮은가격순 ,2=최근 등록순

let getLoadPageEvent = () => {
    bookCategories.forEach(book => {
        book.addEventListener('click', () => {
            category = book.value
            let url = '/api/v1/book/list?' + 'page=0&' + 'category=' + category
            getData(url)
                .then(response => {
                    renderBooks(response.content)
                })
        })
    })
}
let bookList = document.querySelector(".book-list");
let renderBooks = (contentList) =>{
    // console.log('contentList:',contentList)
    bookList.innerHTML='';
    contentList.forEach((content)=>{
        // console.log('content:',content);
        if(!content.rentAvailable){
            bookList.innerHTML +=
                `<label class="flex book-container">
            <input type="checkbox" name="book" value="${content.id}" disabled>
                <div class="book-block red">
                    <div class="book-header flex">
                        <div>${content.bookName}</div>
                        <div>${content.rentPrice} 원</div>
                    </div>
                    <div class="book-footer flex">
                        <div>ISBN : ${content.isbn}</div>
                        <div>위탁자 : ${content.author.name}</div>
                    </div>
                </div>
        </label>`
        }else {
            bookList.innerHTML +=
                `<label class="flex book-container">
            <input type="checkbox" name="book" value="${content.id}">
                <div class="book-block">
                    <div class="book-header flex">
                        <div>${content.bookName}</div>
                        <div>${content.rentPrice} 원</div>
                    </div>
                    <div class="book-footer flex">
                        <div>ISBN : ${content.isbn}</div>
                        <div>위탁자 : ${content.author.name}</div>
                    </div>
                </div>
        </label>`
        }
    })
}

let rentButton = document.querySelector(".rent-button")
let rentalEvent = () => {
    rentButton.addEventListener('click', () => {
        let url = '/api/v1/book/rental';
        let bookIdList = [];
        let bookList = document.querySelector(".book-list");
        if(bookList.querySelectorAll("input[type=checkbox]:checked").length===0) {
            alert('대여할 도서를 선택해 주세요')
            return
        }
        bookList.querySelectorAll("input[type=checkbox]:checked").forEach((box) => {
            bookIdList.push(box.value);
        })
        console.log('id list:',bookIdList)
        postData(url,bookIdList).then(response=>{
            alert(response.message)
            location.reload()
        })
    })
}
rentalEvent();
getLoadPageEvent();
