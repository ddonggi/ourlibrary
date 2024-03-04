package com.example.ourlibrary;

import com.example.ourlibrary.books.*;
import com.example.ourlibrary.user.SiteUser;
import com.example.ourlibrary.user.SiteUserRepository;
import com.example.ourlibrary.user.SiteUserService;
import com.example.ourlibrary.user.UserCreateForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OurlibraryApplicationTests {

	@Autowired
	private SiteUserService siteUserService;
	@Autowired
	private SiteUserRepository siteUserRepository;
	@Autowired
	private BookService bookService;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private RentalRepository rentalRepository ;


	public SiteUser createUserForTest(){
		String name = "이동기";
		String password = "kkwe1068";
		String phoneNumber = "01055370157";
		String email="ldg6153@gmail.com";

		UserCreateForm userCreateForm = UserCreateForm.builder()
				.name(name).email(email).phoneNumber(phoneNumber).password(password).build();
		if(siteUserService.findByEmail(email)!=null){
			userCreateForm = UserCreateForm.builder()
					.name("테스트유저2").email("test2mail@gmail.com").phoneNumber(phoneNumber).password(password).build();
		}
		SiteUser siteUser = siteUserService.create(userCreateForm);
		return siteUser;
	}

	public void consignmentTestBooks(SiteUser siteUser){
		BookForm bookForm = new BookForm();
		String bookName = "테스트도서"; //책 등록
		for(int i=0; i<100; i++) {
			bookForm.setBookName(bookName+(i+1));
			bookForm.setISBN(1919191919111L+i);
			bookForm.setRentPrice(1500+(i+1));
			bookService.consignment(bookForm, siteUser);
		}
	}

	@Test
	@DisplayName("회원가입 Service 테스트")
	@Transactional
	public void signUpTest(){
		//given
		createUserForTest();
		String email="ldg6153@gmail.com";

		//when
		SiteUser siteUser = siteUserService.findByEmail(email);

		//then
		System.out.println("siteUser = " + siteUser);
		assertEquals(siteUser.getEmail(),email);
	}
	@Test
	@Transactional
	@DisplayName("도서 위탁 Service 테스트")
	public void consignmentTest(){
		//given
		SiteUser siteUser = createUserForTest();

		BookForm bookForm = new BookForm();
		String bookName = "테스트도서";
		bookForm.setBookName(bookName);
		bookForm.setISBN(1919191919112L);
		bookForm.setRentPrice(1500);
		//when
		Book book = bookService.consignment(bookForm,siteUser);

		//then
		List<Book> bookList = bookService.findBookListByName(bookName);
		assertEquals(book.getId(),bookList.get(0).getId());
	}

	@Test
	@DisplayName("책 Paging Service Test")
	@Transactional
	public void pageTest(){
		//given
		SiteUser siteUser = createUserForTest();//유저 생성
		consignmentTestBooks(siteUser);//테스트도서 100권 등록

		int category = 1;//0,or else 대여 많은 순 / 1, 낮은 가격순 / 2 최근등록순 /
		int page = 0;

		//when
		Page<BookResponseDTO> bookPage = bookService.getBookPage(page,category);
		//then
		bookPage.stream().map(BookResponseDTO::getRentPrice).forEach(System.out::println);
//		bookPage.stream().map(BookResponseDTO::getRegistrationDate).forEach(System.out::println);
//		bookPage.stream().map(BookResponseDTO::getRentCount).forEach(System.out::println);
	}

	@Test
	@DisplayName("도서 대여 Service Test")
	@Transactional
	public void rentalTest(){
		//given
		//빌리는사람
		SiteUser borrower = createUserForTest();
		//도서 위탁
		consignmentTestBooks(borrower);

		//대여할 도서 목록 생성
		long[] bookIds = {1,3};
		List<Book> bookList =new ArrayList<>();
		// 있는지 조회 후 북리스트에 등록
        for (long id : bookIds) {
            if (bookRepository.findById(id).isPresent())
                bookList.add(bookRepository.findById(id).get());
        }
		System.out.println("for rent bookList = " + bookList);

		long borrowerId = borrower.getId();
		System.out.println("borrowerId = " + borrowerId);

		//when
		//도서 대여
		bookService.rentalBooks(bookList,borrower);

		//then
//		List<Rental> rentalList = rentalRepository.findAllByBorrowerId(borrowerId);
		List<Rental> rentalList = rentalRepository.findByBorrowerIdAndRentalDateBetween(borrowerId, LocalDateTime.now().minusDays(8),LocalDateTime.now().plusDays(8));

		System.out.println("내 대여 리스트 = ");
		for(Rental rental :rentalList){
			System.out.println(rental.getBook().getBookName());
		}

	}

	@Test
	@DisplayName("도서 반납 Service Test")
	@Transactional
	public void returnTest(){
		//given
		//반납하는 사람
		SiteUser borrower = createUserForTest();
		long borrowerId = borrower.getId();
		System.out.println("borrowerId = " + borrowerId);
		//도서 위탁
		consignmentTestBooks(borrower);

		//반납할 도서 정보 생성
		long[] bookIds = {1,3};
		List<Book> bookList =new ArrayList<>();
        for (long bookId : bookIds) {
            if (bookRepository.findById(bookId).isPresent())
                bookList.add(bookRepository.findById(bookId).get());
        }
		System.out.println("for return bookList = " + bookList);

		//when
		//도서 대여
		bookService.rentalBooks(bookList,borrower);
		//도서 반납
		bookService.returnBooks(bookList,borrower);

		//then
		List<Rental> rentalList = rentalRepository.findAllByBorrowerId(borrowerId);
		for(Rental rental:rentalList){
			System.out.println("도서 = " + rental.getBook().getBookName());
			System.out.println("반납 일자 = " + rental.getReturnDate());
			System.out.println("반납자 = " + rental.getBorrower().getName());
		}
	}

	@Test
	@DisplayName("대여 중인 도서 대여 Service Test")
	@Transactional
	public void rentedBookRentTest(){
		//given
		//대여하는 사람
		SiteUser borrower1 = createUserForTest();
		SiteUser borrower2 = createUserForTest();

		//도서 위탁
		consignmentTestBooks(borrower1);

		//대여할 도서 정보 생성
		long[] bookIds = {1,3,4,5};
		List<Book> bookList =new ArrayList<>();
		for (long bookId : bookIds) {
			if (bookRepository.findById(bookId).isPresent())
				bookList.add(bookRepository.findById(bookId).get());
		}

		//when
		//유저 1 도서 대여
		System.out.println("borrower1 = " + borrower1.getName());
		bookService.rentalBooks(bookList,borrower1);

		//대여할 도서 정보 생성
//		bookIds = new long[]{1,3,4,5}; //이미 대여중인 책
		bookIds = new long[]{7,8}; //대여중이지 않은 책
		bookList =new ArrayList<>();
		for (long bookId : bookIds) {
			if (bookRepository.findById(bookId).isPresent())
				bookList.add(bookRepository.findById(bookId).get());
		}
		//유저 2 도서 대여
		System.out.println("borrower2 = " + borrower2.getName());
		bookService.rentalBooks(bookList,borrower2);

		//then
		List<Rental> rentalList = rentalRepository.findAllByBorrowerId(borrower2.getId());
		for(Rental rental:rentalList){
			System.out.println("대여 도서 = " + rental.getBook().getBookName());
		}
	}
}
