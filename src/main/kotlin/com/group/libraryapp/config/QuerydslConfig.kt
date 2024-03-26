package com.group.libraryapp.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

@Configuration
class QuerydslConfig(
    private val em: EntityManager,
) {

    @Bean //querydsl 이라는 이름의 빈 등록
    fun querydsl(): JPAQueryFactory {
        return JPAQueryFactory(em)
    }
}