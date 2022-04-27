package edu.uwb.gymapp.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @RequestMapping("/members")
    public List<Member> getAllMembers() {
        return memberService.getAllTopics();
    }

//    @RequestMapping("/members/{username}")
//    public Member getMember(@PathVariable String username) {
//        return memberService.getMember(username);
//    }

    @RequestMapping("/members/{id}")
    public Member getMember(@PathVariable Long id) {
        return memberService.getMember(id);
    }

    @RequestMapping(method= RequestMethod.POST, value="/members")
    public void addMember(@RequestBody Member member) {
        memberService.addMember(member);
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
