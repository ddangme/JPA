package ddangme.jpa.service;

import ddangme.jpa.domain.Member;
import ddangme.jpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입() throws Exception {
        // Given
        Member member = new Member();
        member.setName("Kim");

        // When
        Long saveId = memberService.join(member);

        // Then
        assertThat(member.getId()).isEqualTo(memberService.findOne(saveId).getId());
    }

    @Test
    void 중복_회원_예외() throws Exception {
        // Given
        Member member1 = new Member();
        member1.setName("Kim");

        Member member2 = new Member();
        member2.setName("Kim");

        memberService.join(member1);
        // When
        assertThatThrownBy(() -> {
            memberService.join(member2);
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 회원입니다.");
    }

}