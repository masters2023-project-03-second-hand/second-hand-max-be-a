package codesquard.app.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import codesquard.app.domain.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
