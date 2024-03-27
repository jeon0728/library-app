package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import com.group.libraryapp.repository.book.BookQuerydslRepository
import com.group.libraryapp.repository.user.loanhistory.UserLoanHistoryQuerydslRepository
import com.group.libraryapp.util.fail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val bookQuerydslRepository: BookQuerydslRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
    private val userLoanHistoryQuerydslRepository: UserLoanHistoryQuerydslRepository,
) {

    @Transactional
    fun saveBook(request: BookRequest) {
        val book = Book(request.name, request.type)
        bookRepository.save(book)
    }

    @Transactional
    fun loanBook(request: BookLoanRequest) {
        val book = bookRepository.findByName(request.bookName) ?: fail()
        if (userLoanHistoryQuerydslRepository.find(request.bookName, UserLoanStatus.LOANED) != null) {
            throw IllegalArgumentException("진작 대출되어 있는 책입니다.")
        }

        val user = userRepository.findByName(request.userName) ?: fail()
        user.loanBook(book)
    }

    @Transactional
    fun returnBook(request: BookReturnRequest) {
        val user = userRepository.findByName(request.userName) ?: fail()
        user.returnBook(request.bookName)
    }

    @Transactional(readOnly = true)
    fun countLoanedBook(): Int {
        // 아래 코드는 쿼리를 실행했을 때 모든 데이터를 가져와서 메모리 로딩 후 사이즈를 구한다.
        // 즉 데이터가 많다면 db및 네트워크의 부하로 인하여 성능 저하를 유발할 수 있다.
        // return userLoanHistoryRepository.findAllByStatus(UserLoanStatus.LOANED).size

        // 아래 코드는 쿼리를 실행했을 때 모든 데이터가 아닌 숫자만 가져온다.
        // 즉 위 코드와는 다르게 db및 네트워크의 부하를 줄일 수 있다.
        return userLoanHistoryQuerydslRepository.count(UserLoanStatus.LOANED).toInt()
    }

    @Transactional(readOnly = true)
    fun getBookStatistics(): List<BookStatResponse> {
        // 아래 코드는 쿼리를 실행했을 때 모든 데이터가 아닌 group by 를 이용해 필요한 데이터만 가져온다.
        // 즉 아래 코드와는 다르게 db및 네트워크의 부하를 줄일 수 있다.
        return bookQuerydslRepository.getStats()

        // 아래 코드는 쿼리를 실행했을 때 모든 데이터를 가져와서 메모리 로딩 후 함수형 프로그램에 그룹핑을 한다.
        // 즉 데이터가 많다면 db에 부하를 가져와 성능 저하를 유발할 수 있다.
        /*
        return bookRepository.findAll() // List<Book>
            .groupBy { book -> book.type } // Map<BookType, List<Book>>
            .map { (type, books) -> BookStatResponse(type, books.size) }
         */


        /* 위와 같이 함수형 코드로 리팩토링
        val results = mutableListOf<BookStatResponse>()
        val books = bookRepository.findAll()

        for (book in books) {
            results.firstOrNull { dto -> book.type == dto.type}?.plusOne()
                ?: results.add(BookStatResponse(book.type, 1))

        }

        return results
         */
    }
}