package site.book.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.UsedBookWish;

public interface UsedBookWishRepository extends JpaRepository<UsedBookWish, Integer> {

}
