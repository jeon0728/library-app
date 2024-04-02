package com.group.libraryapp.domain.book

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Book(
    val name: String,

    @Enumerated(EnumType.STRING)
    val type: BookType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

    init {
        if(name.isBlank()) {
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다.")
        }
    }


    // 엔티티에 속성이 추가 될때마다 테스트 코드를 고쳐야 하는 번거로움을 줄이기 위해 쓰는 방법
    // 테스트에 이용할 객체를 만드는 함수를 Object Mother 이라고 한다.
    // 이렇게 테스트를 하기위해 생겨난 테스트용 객체를 Test Fixture 라고 한다.
    companion object {
        fun fixture(
            name: String = "책 이름",
            type: BookType = BookType.COMPUTER,
            id: Long? = null,
        ): Book {
            return Book(
                name = name,
                type = type,
                id = id,
            )
        }

    }
}