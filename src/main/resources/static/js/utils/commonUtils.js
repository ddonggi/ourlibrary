/* 비동기 통신에 쓰일 보안용 CSRF 정보 */
let csrf_header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
let csrf_token = document.querySelector("meta[name='_csrf']").getAttribute("content");

/* 비동기통신을 위한 fetch API */

let getData = async (url, csrf_header_=csrf_header, csrf_token_=csrf_token) => {
    // 옵션 기본 값은 *로 강조
    const response = await fetch(url, {
        method: "GET", // *GET, POST, PUT, DELETE 등
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": "application/json",
            // 'Content-Type': 'application/x-www-form-urlencoded',
            header: csrf_header,
            'X-CSRF-Token': csrf_token,
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        // body: JSON.stringify(data), // body의 데이터 유형은 반드시 "Content-Type" 헤더와 일치해야 함
    });
    return response.json(); // JSON 응답을 네이티브 JavaScript 객체로 파싱
}


let postData = async (url, data = {}, csrf_header_=csrf_header, csrf_token_=csrf_token) => {
    console.log('data:',data)
    // 옵션 기본 값은 *로 강조
    const response = await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE 등
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": "application/json",
            // 'Content-Type': 'application/x-www-form-urlencoded',
            header: csrf_header,
            'X-CSRF-Token': csrf_token
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(data), // body의 데이터 유형은 반드시 "Content-Type" 헤더와 일치해야 함
    });
    return response.json(); // JSON 응답을 네이티브 JavaScript 객체로 파싱
}

/*글자입력에 따른 변화를 트래킹하는 함수*/
let setTextChangeTrackingEvent = (element) =>{
    let maxLength = element.maxLength;
    console.log('maxLength:',maxLength)
    let minLength = element.minLength;
    console.log('minLength:',minLength)
    element.parentElement.parentElement.querySelector(".text-limit-max").textContent='/'+maxLength;
    let currentContainer = element.parentElement.parentElement.querySelector(".text-limit-current");
    currentContainer.textContent = element.value.length;
    element.addEventListener('input',()=>{
        console.log('change textContent:',element.value.length)
        currentContainer.textContent = element.value.length;
        if(element.value.length>=maxLength)currentContainer.classList.add("text-warning")
        else if(element.value.length<minLength)currentContainer.classList.add("text-warning")
        else currentContainer.classList.remove("text-warning")
    })
}
let setAsyncNickNameCheckEvent = (element,isProfilePage)=>{
    element.addEventListener('input', inputDebounce(() => saveInput(element,isProfilePage)))
}

function inputDebounce(func, timeout = 500) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(this, args);
        }, timeout);
    };
}

let regex = /^[가-힣a-zA-Z0-9]*$/;
function saveInput(element,isProfilePage) {
    // console.log('Saving data:',element);
    // element.value
    // console.log(inputText.indexOf(" "))
    let submitButton = document.querySelector(".submit-button");
    let inputText = element.value;
    if(!regex.test(inputText)){
        console.log('공백은 안되염')
        //저장 비활 //disable /pointer
        submitButton.setAttribute('disabled',true);
        submitButton.classList.add("disabled");
        element.nextElementSibling.classList.remove("text-good");
        element.nextElementSibling.textContent='사용할 수 없는 패턴 입니다';
    } else if(inputText.length<2){
        submitButton.setAttribute('disabled',true);
        submitButton.classList.add("disabled");
        element.nextElementSibling.classList.remove("text-good");
        element.nextElementSibling.textContent='닉네임은 최소 2글자 이상 필요합니다';
    }
    else{
        //저장버튼 활성
        // pointer
        let isProfile = isProfilePage
        console.log('is profile??:',isProfile);
        if(isProfilePage) {
            submitButton.classList.remove("disabled");
            submitButton.removeAttribute('disabled');
        }
        postData('/user/name/check',{username:inputText},csrf_header,csrf_token).then(response => {
            if(response.result==="positive") {
                element.nextElementSibling.classList.add("text-good");
            }else {
                element.nextElementSibling.classList.remove("text-good");
            }
            element.nextElementSibling.textContent=response.message;
        })
        //
    }
}

export {postData,getData}
