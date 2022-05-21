package edu.uwb.gymapp.resources;

import edu.uwb.gymapp.models.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/user-management/api/v1")
public class MemberController {

    Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    /**
     * Get list of all members in the database
     * @return List of all members in the database
     */
    @RequestMapping("/members")
    public List<Member> getAllMembers() {
        logger.info("All members information has been retrieved.");
        return memberService.getAllMembers();
    }

    /**
     * Get all the information about the member with the give email address
     * @param email The email address of the member
     * @return  The information about the given member
     */
    @RequestMapping(value="/members", params="email", method = RequestMethod.GET)
    public Member getMember(@RequestParam("email") String email) {
        logger.info("Information has been retrieved for member with email: " + email);
        return memberService.getMember(email);
    }

    /**
     * Get all the information about the member with the given id
     * @param id The id of the member
     * @return The information about the given member
     */
    @RequestMapping("/members/{id}")
    public Member getMember(@PathVariable Long id) {
        logger.info("Information has been retrieved for member with id: " + id);
        return memberService.getMember(id);
    }

    /**
     * Add a new member to the database. The member information must be given in the request body in JSON format.
     * @param member    JSON object with all member information
     * @return  Success or failure message string
     */
    @RequestMapping(method= RequestMethod.POST, value="/member/signup")
    public ResponseEntity<String> addMember(@RequestBody Member member) {
        try {
            Member newMember = memberService.addMember(member);
            logger.info("Successfully created profile for member with email: " + member.getEmail());
            return new ResponseEntity<>("Your profile was successfully created with set-slot-sweat.", HttpStatus.OK);
        } catch (DataIntegrityViolationException ex) {
            logger.info("Sign Up Failed. Email already exists in the system:" + member.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sign Up Failed. Email already exists in the system: " + member.getEmail());
        } catch (RuntimeException ex) {
            logger.debug("Failed to create member profile.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Failed to create user profile. Review your information and try again.");
        }
    }

    /**
     * Update information for the given member
     * @param member    JSON object with all member information
     */
    @RequestMapping(method=RequestMethod.PUT, value="/members/{id}")
    public void updateMember(@RequestBody Member member) {
        memberService.updateMember(member);
        logger.info("Record has been modified for member with email: " + member.getEmail());
    }

    /**
     * Delete member with the given id
     * @param id    The id of the member to be deleted
     */
    @RequestMapping(method=RequestMethod.DELETE, value="/members/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        logger.info("Deleted member with id: " + id);
    }
}
