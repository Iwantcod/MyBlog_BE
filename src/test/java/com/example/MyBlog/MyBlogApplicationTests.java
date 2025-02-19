package com.example.MyBlog;

import com.example.MyBlog.domain.image.entity.Image;
import com.example.MyBlog.domain.image.repository.ImageRepository;
import com.example.MyBlog.domain.image.service.ImageService;
import com.example.MyBlog.domain.member.DTO.AuthDTO;
import com.example.MyBlog.domain.member.DTO.JoinDTO;
import com.example.MyBlog.domain.member.details.JwtUserDetails;
import com.example.MyBlog.domain.member.entity.Member;
import com.example.MyBlog.domain.member.repository.MemberRepository;
import com.example.MyBlog.domain.member.service.MemberService;
import com.example.MyBlog.domain.post.DTO.RequestAddPostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostDTO;
import com.example.MyBlog.domain.post.DTO.ResponsePostListDTO;
import com.example.MyBlog.domain.post.entity.Post;
import com.example.MyBlog.domain.post.repository.PostRepository;
import com.example.MyBlog.domain.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class MyBlogApplicationTests {

    @Autowired
    private MemberService memberService;
	@Autowired
	private PostService postService;
	@Autowired
	private ImageService imageService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Test
	@Transactional // 트랜젝션 적용
	@Rollback(false) // 테스트 코드 이후 롤백하지 않도록 막기
	void contextLoads() {
		// 테스트를 위한 인증 정보 추가
		AuthDTO authDTO = new AuthDTO();
		authDTO.setUsername("Andy");
		authDTO.setRoleType("ROLE_USER");
		authDTO.setPassword("tempPW");
		JwtUserDetails jwtUserDetails = new JwtUserDetails(authDTO);
		Authentication authToken = new UsernamePasswordAuthenticationToken(jwtUserDetails, null, jwtUserDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		long postId = 206L;
		try {
			imageService.deleteImageByPostId(postId);
			postService.deletePost(postId);
		} catch (Exception e) {
			e.printStackTrace();
		}




//		for (int i = 0; i < 10; i++) {
//			JoinDTO joinDTO = new JoinDTO();
//			joinDTO.setUsername("member" + i);
//			joinDTO.setAge(10 + i);
//			joinDTO.setName("dummyMember" + i);
//			joinDTO.setPassword("1234");
//			memberService.join(joinDTO);
//		}


//		// 이미지 업로드 테스트
//		// 1. Post Id 얻어내기
//		Long postId = 1L;
//
//		try {
//			// 2. src/test/resources/images/test.jpg 파일을 읽어온다.
//			// ClassPathResource로 리소스를 읽고, FileCopyUtils.copyToByteArray()로 바이트 배열로 변환
//			ClassPathResource imageResource = new ClassPathResource("images/clock.jpg");
//			byte[] imageBytes = FileCopyUtils.copyToByteArray(imageResource.getInputStream());
//
//			// 3. MockMultipartFile 객체 생성
//			// 첫번째 인자는 form field 이름, 두번째는 파일 이름, 세번째는 Content-Type, 네번째는 파일 내용
//			MockMultipartFile mockFile = new MockMultipartFile(
//					"file",
//					"clock.jpg",
//					"image/jpeg",
//					imageBytes
//			);
//
//			List<MultipartFile> fileList = Collections.singletonList(mockFile);
//
//			// 4. addImages 메소드 호출
//			imageService.addImages(fileList, postId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
