package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/user-management/api/v1")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @RequestMapping("/members")
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @RequestMapping(value="/members", params="email", method = RequestMethod.GET)
    public Member getMember(@RequestParam("email") String email) {
        return memberService.getMember(email);
    }

    @RequestMapping("/members/{id}")
    public Member getMember(@PathVariable Long id) {
        return memberService.getMember(id);
    }

    @RequestMapping(method= RequestMethod.POST, value="/member/signup")
    public ResponseEntity<?> addMember(@RequestBody Member member) {
        try {
            Member newMember = memberService.addMember(member);
            return new ResponseEntity<>(newMember, HttpStatus.OK);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Failed to create user profile. Review your information and try again.");
        }
    }

    @RequestMapping(method=RequestMethod.PUT, value="/members/{id}")
    public void updateMember(@RequestBody Member member) {
        memberService.updateMember(member);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/members/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }
}
