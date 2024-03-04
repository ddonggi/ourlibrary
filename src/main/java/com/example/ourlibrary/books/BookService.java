package com.example.ourlibrary.books;

import com.example.ourlibrary.user.SiteUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PackageName : com.example.ourlibrary.books
 * FileName : BookService
 * Author : dglee
 * Create : 3/2/24 10:46 PM
 * Description :
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;


    //도서 위탁
    public Book consignment(BookForm bookForm, SiteUser siteUser) {
        Book book = bookForm.toEntity(siteUser);
        return bookRepository.save(book);
    }

    //화면에 보여줄 책 페이징 불러오는 함수
    public Page<BookResponseDTO> getBookPage(int page, int category) {
        List<Sort.Order> sortedList = new ArrayList<>();
        String property;
        if (category == 1) {//가격낮은순
            property = "rentPrice";
            sortedList.add(Sort.Order.asc(property));
        } else if (category == 2) {//최근 등록순
            property = "registrationDate";//최근등록순
            sortedList.add(Sort.Order.desc(property));
        } else {//default : 대여 많은 순
            property = "rentCount";//대여 많은 순
            sortedList.add(Sort.Order.desc(property));
        }

        Pageable pageable = PageRequest.of(page, 20, Sort.by(sortedList));
//        questionRepository.findAllByAuthor(siteUser,pageable);
        Page<BookResponseDTO> bookPage = bookRepository.findAll(pageable).map(BookResponseDTO::toDTO);
        return bookPage;
    }

    //도서 대여 함수
    public String rentalBooks(List<Book> bookList, SiteUser borrower) {
        for (Book book : bookList) {
            if(!book.getRentAvailable()) {
                System.out.println("도서 id:"+book.getId()+"/ 도서명:"+book.getBookName()+"/은 이미 대여중입니다. 체크 해제 후 다시 대여를 눌러주세요.");
                return book.getBookName() + " 는 이미 대여중입니다";
            }
        }
        for (Book book : bookList) {
            if (book.getRentAvailable()) {//대여 가능할때만
                rentalRepository.save(Rental.builder()
                                .limitDate(LocalDateTime.now().plusSeconds(10))
                                .rentalDate(LocalDateTime.now())
                                .returnDate(null)
                                .book(book)
                                .borrower(borrower)
                            .build());
                modifyBook(book, false);
                System.out.println("도서 id:"+book.getId()+"/ 도서명:"+book.getBookName()+"/해당 도서를 대여 하였습니다");
            }
        }
        return "대여가 완료되었습니다";
    }

    //도서 반납 함수
    public void returnBooks(List<Book> bookList, SiteUser borrower) {
        for (Book book : bookList) {
            if (!book.getRentAvailable()) {//반납 가능 할때만

//                Rental rental = rentalRepository.findRentalByBookIdAndBorrowerId(book.getId(),borrower.getId());
                //현재보다 20초 전후에 있는 도서
                Rental rental = rentalRepository.findByBorrowerIdAndBookIdAndRentalDateBetween(borrower.getId(),book.getId(),LocalDateTime.now().minusSeconds(20),LocalDateTime.now().plusSeconds(20));
//                Rental rental = rentalRepository.findByBorrowerIdAndRentalDateBetweenAndLimitDateAfter(book.getId(),borrower.getId());

                rentalRepository.save(
                        Rental.builder()
                        .id(rental.getId())
                        .rentalDate(rental.getRentalDate())
                        .limitDate(rental.getLimitDate())
                        .returnDate(LocalDateTime.now())
                        .book(book)
                        .borrower(borrower)
                        .build());
                modifyBook(book, true);
                System.out.println("도서 id:"+book.getId()+"/ 도서명:"+book.getBookName()+" /해당 도서를 반납 하였습니다");
            }else{
                System.out.println("도서 id:"+book.getId()+"/ 도서명:"+book.getBookName()+" /해당 도서는 이미 반납처리 되었습니다.");
            }
        }
    }

    public void modifyBook(Book book, boolean available) {
        int rentCount=book.getRentCount();
        if(!available){//도서 대여시
            rentCount +=1;
        }
        book = Book.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .registrationDate(book.getRegistrationDate())
                .rentPrice(book.getRentPrice())
                .rentCount(rentCount)
                .ISBN(book.getISBN())
                .author(book.getAuthor())
                .rentAvailable(available)
                .build();
        bookRepository.save(book);
    }


    public List<Book> findBookListByName(String bookName) {
        Optional<List<Book>> optionalBookList = bookRepository.findAllByBookName(bookName);
        List<Book> bookList = new ArrayList<>();
        if (optionalBookList.isPresent())
            bookList = optionalBookList.get();
        return bookList;
    }
}
