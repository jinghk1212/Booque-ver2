package site.book.project.web;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.Post;
import site.book.project.domain.User;
import site.book.project.dto.HomeTopFiveListDto;
import site.book.project.dto.NoticeDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.service.HomeService;
import site.book.project.service.NoticeService;
import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final HomeService homeService;
    private final UserService userService;
    
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserSecurityDto userSecurityDto) {
        log.info("home()");
        
        // 전체 책 별점순 1~8위
        List<Book> list = homeService.readAllRankingOrderByBookScore();
        
        // 전체 책 리뷰많은순 1~8위
        List<Book> postList = homeService.readAllRankingOrderByPostReview();
        
        // 분야별 별점순/리뷰많은 순 순위(별점순)
        String category = "";

        // 경제/경영
        category = "경제/경영";
        List<Book> economyScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);
        
        // 인문
        category = "인문";
        List<Book> humanitiesScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);
        
        // 소설
        category = "소설";
        List<Book> fictionScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);

        // 시/에세이
        category = "시/에세이";
        List<Book> essayScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);

        // 자기계발
        category = "자기계발";
        List<Book> selpHelpScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);
        
        // 분야별 별점순/리뷰많은 순 순위(리뷰순)
        category = "경제/경영";
        List<Book> economyPostList = homeService.readAllRankingCategoryOrderByBookReview(category);
        
        // 인문
        category = "인문";
        List<Book> humanitiesPostList = homeService.readAllRankingCategoryOrderByBookReview(category);
       
        // 소설
        category = "소설";
        List<Book> fictionPostList = homeService.readAllRankingCategoryOrderByBookReview(category);
        
        // 시/에세이
        category = "시/에세이";
        List<Book> essayPostList = homeService.readAllRankingCategoryOrderByBookReview(category);
        
        // 자기계발
        category = "자기계발";
        List<Book> selpHelpPostList = homeService.readAllRankingCategoryOrderByBookReview(category);
        
        // 전체 포스트(리뷰) 중 댓글이 많이 달린 순 1~5위
        List<HomeTopFiveListDto> hotReviewPostList = homeService.readTopFiveHotReviewOrderByPost();
        for (HomeTopFiveListDto b : hotReviewPostList) {
            log.info("id={},score={},bookname={}", b.getPostId(), b.getHit(), b.getBookName());
          }
        
        // 전체 포스트(조회수순) 1~5위
        List<HomeTopFiveListDto> bestHitPostList = homeService.readTopFiveBestHitOrderByPost();
        for (HomeTopFiveListDto b : bestHitPostList) {
          log.info("id={},score={},bookname={}", b.getPostId(), b.getHit(), b.getBookName());
        }
       
        if(userSecurityDto != null) { 
            // (예진) 홈 로그인시 nickName으로 교체
           String nick = userSecurityDto.getNickName();
           model.addAttribute("nick", nick); 
           
           // (예진) 알림 리스트 불러오기위해 userId
           Integer userId = userSecurityDto.getId();
           model.addAttribute("userId", userId); 
        }
        
        
        model.addAttribute("top4ScoreList", list);                     // 전체 책 별점순 1~8위
        model.addAttribute("top4ReviewList", postList);                // 전체 책 리뷰많은순 1~8위
        model.addAttribute("economyScoreList", economyScoreList);       // 경제/경영(별점)
        model.addAttribute("humanitiesScoreList", humanitiesScoreList); // 인문(별점)
        model.addAttribute("fictionScoreList", fictionScoreList);       // 소설(별점)
        model.addAttribute("essayScoreList", essayScoreList);           // 시/에세이(별점)
        model.addAttribute("selpHelpScoreList", selpHelpScoreList);     // 자기계발(별점)
        model.addAttribute("economyPostList", economyPostList);         // 경제/경영(리뷰순)
        model.addAttribute("humanitiesPostList", humanitiesPostList);   // 인문(리뷰순)
        model.addAttribute("fictionPostList", fictionPostList);         // 소설(리뷰순)
        model.addAttribute("essayPostList", essayPostList);             // 시/에세이(리뷰순)
        model.addAttribute("selpHelpPostList", selpHelpPostList);       // 자기계발(리뷰순)
        model.addAttribute("hotReviewPostList", hotReviewPostList);     // 댓글 많이 달린 Top 1~5위 리뷰글
        model.addAttribute("bestHitPostList", bestHitPostList);         // 조회수 많은 Top 1~5위 베스트글 
        
        return "home";
    }
  
}
