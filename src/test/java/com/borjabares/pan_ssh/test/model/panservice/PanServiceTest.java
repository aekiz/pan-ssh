package com.borjabares.pan_ssh.test.model.panservice;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.borjabares.modelutil.exceptions.InstanceNotFoundException;
import com.borjabares.pan_ssh.exceptions.*;
import com.borjabares.pan_ssh.model.category.Category;
import com.borjabares.pan_ssh.model.comment.Comment;
import com.borjabares.pan_ssh.model.commentvote.CommentVote;
import com.borjabares.pan_ssh.model.links.Links;
import com.borjabares.pan_ssh.model.linkvote.LinkVote;
import com.borjabares.pan_ssh.model.panservice.FullComment;
import com.borjabares.pan_ssh.model.panservice.ObjectBlock;
import com.borjabares.pan_ssh.model.panservice.PanService;
import com.borjabares.pan_ssh.model.report.Report;
import com.borjabares.pan_ssh.model.user.User;
import com.borjabares.pan_ssh.util.GlobalNames.LinkStatus;
import com.borjabares.pan_ssh.util.GlobalNames.ReportStatus;
import com.borjabares.pan_ssh.util.GlobalNames.VoteType;
import com.borjabares.pan_ssh.util.PasswordCodificator;
import com.borjabares.pan_ssh.util.GlobalNames.Level;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContextTest.xml")
@Transactional
public class PanServiceTest {

	@Autowired
	private PanService panService;

	private final static long NON_EXISTENT_ID = -1;

	@Test
	public void createAndFindUser() throws Exception {
		User user1 = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		User user2 = panService.findUser(user1.getUserId());

		assertEquals(user1, user2);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentUserId() throws InstanceNotFoundException {
		panService.findUser(NON_EXISTENT_ID);
	}

	@Test(expected = DuplicatedUserLoginException.class)
	public void createDuplicatedLoginUsers() throws Exception {
		User user1 = new User("Luis", "Luis", "luis1@gmail.com", "192.168.1.1");
		User user2 = new User("luis", "Luis", "luis2@gmail.com", "192.168.1.1");
		panService.createUser(user1);
		panService.createUser(user2);
	}

	@Test(expected = DuplicatedUserEmailException.class)
	public void createDuplicatedEmailUsers() throws Exception {
		User user1 = new User("Luis1", "Luis", "luis@gmail.com", "192.168.1.1");
		User user2 = new User("Luis2", "Luis", "luis@gmail.com", "192.168.1.1");
		panService.createUser(user1);
		panService.createUser(user2);
	}

	@Test
	public void findUserByLevel() throws Exception {
		User user1 = new User("Andrea", "Andrea", "andrea@gmail.com",
				"192.168.1.1");
		User user2 = new User("Borja", "Borja", "borja@gmail.com",
				"192.168.1.1");
		user1.setLevel(Level.GOD);
		user1 = panService.createUser(user1);
		user2 = panService.createUser(user2);

		ObjectBlock<User> userBlock = panService.listUsersByLevel(0, 10,
				Level.GOD);

		List<User> users = userBlock.getList();

		assertEquals(users.size(), 1);
		assertEquals(users.get(0), user1);
	}

	@Test
	public void modifyUser() throws Exception {
		User user1 = panService.createUser(new User("Juan", "Juan",
				"juan@gmail.com", "192.168.1.1"));
		user1.setUsername("Pedro");
		panService.updateUser(user1);
		User user2 = panService.findUser(user1.getUserId());

		assertEquals(user1.getUsername(), user2.getUsername());

	}

	@Test(expected = InstanceNotFoundException.class)
	public void deleteUser() throws Exception {
		User user = panService.createUser(new User("Luis", "Luis",
				"luis@gmail.com", "192.168.1.1"));
		assertNotNull(user.getUserId());
		panService.deleteUser(user.getUserId());
		panService.findUser(user.getUserId());
	}

	@Test
	public void login() throws Exception {
		String password = "Este1Es2El8Password";
		User user = panService.createUser(new User("Marcos", password,
				"marcos@gmail.com", "192.168.1.1"));
		long id = panService.loginUser(user.getLogin(),
				PasswordCodificator.codify(password));

		assertEquals(user.getUserId(), id);
	}

	@Test
	public void createAndFindCategory() throws InstanceNotFoundException,
			ParentCategoryException, DuplicatedCategoryNameException {
		Category category1 = panService.createCategory(new Category(
				"Actualidad", null));
		Category category2 = panService.findCategory(category1.getCategoryId());

		assertEquals(category1, category2);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void createNonExistentCategoryId() throws InstanceNotFoundException {
		panService.findCategory(NON_EXISTENT_ID);
	}

	@Test(expected = DuplicatedCategoryNameException.class)
	public void createDuplicatedCategories() throws Exception {
		panService.createCategory(new Category("Actualidad", null));
		panService.createCategory(new Category("Actualidad", null));
	}

	@Test
	public void listParentCategories() throws Exception {
		Category category1 = panService.createCategory(new Category(
				"Actualidad", null));
		panService.createCategory(new Category("Novedades", category1));
		panService.createCategory(new Category("Corazón", category1));
		panService.createCategory(new Category("Deportes", null));

		List<Category> categories = panService.listParentCategories();

		assertEquals(categories.size(), 2);
	}

	@Test
	public void listNonParentCategories() throws Exception {
		Category category1 = panService.createCategory(new Category(
				"Actualidad", null));
		panService.createCategory(new Category("Novedades", category1));
		panService.createCategory(new Category("Corazón", category1));
		Category category2 = panService.createCategory(new Category("Deportes",
				null));
		panService.createCategory(new Category("Formula 1", category2));
		panService.createCategory(new Category("Fútbol", category2));

		List<Category> categories = panService.listNonParentCategories();

		assertEquals(categories.size(), 4);
	}

	@Test
	public void listCategoryChildrens() throws Exception {
		Category category1 = panService.createCategory(new Category(
				"Actualidad", null));
		panService.createCategory(new Category("Novedades", category1));
		panService.createCategory(new Category("Corazón", category1));
		Category category2 = panService.createCategory(new Category("Deportes",
				null));
		panService.createCategory(new Category("Formula 1", category2));
		panService.createCategory(new Category("Fútbol", category2));

		List<Category> childrens = panService.listCategoryChildrens(category1
				.getCategoryId());

		assertEquals(childrens.size(), 2);
	}

	@Test
	public void createAndFindLink() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link1 = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));

		Links link2 = panService.findLink(link1.getLinkId());
		assertEquals(link1, link2);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentLinkId() throws InstanceNotFoundException {
		panService.findLink(NON_EXISTENT_ID);
	}

	@Test(expected = DuplicatedLinkException.class)
	public void createDuplicatedLink() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		panService.createLink(new Links("http://www.google.es", "Google",
				"HomePage of Google", "Google, search", user, category));
		panService.createLink(new Links("http://www.google.es", "Bing",
				"HomePage of Bing", "Bing, search", user, category));
	}

	@Test
	public void createAndFindReport() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		Report report1 = panService.createReport(new Report(link, user,
				"Enlace duplicado"));

		Report report2 = panService.findReport(report1.getReportId());

		assertEquals(report1, report2);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistenReportId() throws InstanceNotFoundException {
		panService.findReport(NON_EXISTENT_ID);
	}

	@Test(expected = LinkAlreadyReportedException.class)
	public void createReportedLink() throws Exception {
		User user1 = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		User user2 = panService.createUser(new User("Julia", "Julia",
				"julia@gmail.com", "32.14.27.186"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user1,
				category));
		panService.createReport(new Report(link, user1, "Enlace duplicado"));
		panService.createReport(new Report(link, user2, "Enlace repetido"));
	}

	@Test
	public void listPendingReports() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link1 = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		Links link2 = panService
				.createLink(new Links("http://www.yahoo.es", "Yahoo",
						"HomePage of Yahoo", "Google, search", user, category));
		Links link3 = panService.createLink(new Links("http://www.bing.es",
				"Bing", "HomePage of Bing", "Bing, search", user, category));
		panService.createReport(new Report(link1, user, "Enlace duplicado"));
		Report report = new Report(link2, user, "Enlace duplicado");
		report.setStatus(ReportStatus.CHECKED);
		panService.createReport(report);
		panService.createReport(new Report(link3, user, "Enlace duplicado"));
		ObjectBlock<Report> reports = panService.listPendingReports(0, 10);
		assertEquals(reports.getPagination().getPageNum(), 1);
		assertEquals(reports.getList().size(), 2);
	}

	@Test
	public void listLinksByUserId() throws Exception {
		User user1 = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		User user2 = panService.createUser(new User("Juan", "Juan",
				"juan@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		panService.createLink(new Links("http://www.google.es", "Google",
				"HomePage of Google", "Google, search", user1, category));
		panService.createLink(new Links("http://www.yahoo.es", "Yahoo",
				"HomePage of Yahoo", "Google, search", user1, category));
		panService.createLink(new Links("http://www.bing.es", "Bing",
				"HomePage of Bing", "Bing, search", user2, category));
		ObjectBlock<Links> links = panService.listLinksByUserId(0, 10,
				user1.getUserId());

		assertEquals(links.getList().size(), 2);
	}

	@Test
	public void listLinksByStatus() throws Exception {
		User user1 = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		User user2 = panService.createUser(new User("Juan", "Juan",
				"juan@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link1 = new Links("http://www.google.es", "Google",
				"HomePage of Google", "Google, search", user1, category);
		link1.setStatus(LinkStatus.PUBLISHED);
		Links link2 = new Links("http://www.yahoo.es", "Yahoo",
				"HomePage of Yahoo", "Google, search", user1, category);
		link2.setStatus(LinkStatus.PUBLISHED);
		panService.createLink(link1);
		panService.createLink(link2);
		panService.createLink(new Links("http://www.bing.es", "Bing",
				"HomePage of Bing", "Bing, search", user2, category));
		ObjectBlock<Links> links = panService.listLinksByStatus(0, 10,
				LinkStatus.PUBLISHED);

		assertEquals(links.getList().size(), 2);
	}

	@Test
	public void createAndFindAnonymousVote() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		LinkVote vote1 = panService.createVote(new LinkVote(link,
				VoteType.UPVOTE, "1.2.3.4"));
		LinkVote vote2 = panService.findVote(vote1.getVoteId());

		assertEquals(vote1, vote2);
	}

	@Test
	public void createAndFindNonAnonymousVote() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		LinkVote vote1 = panService.createVote(new LinkVote(link, user,
				VoteType.UPVOTE, "1.2.3.4"));
		LinkVote vote2 = panService.findVote(vote1.getVoteId());

		assertEquals(vote1, vote2);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentVote() throws Exception {
		panService.findVote(NON_EXISTENT_ID);
	}

	@Test(expected = IpVotedException.class)
	public void createDuplicatedIpAnonymousVote() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		panService.createVote(new LinkVote(link, user, VoteType.UPVOTE,
				"1.2.3.4"));
		panService.createVote(new LinkVote(link, VoteType.DOWNVOTE, "1.2.3.4"));
	}

	@Test(expected = UserVotedException.class)
	public void createDuplicatedVote() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		panService.createVote(new LinkVote(link, user, VoteType.UPVOTE,
				"1.2.3.4"));
		panService.createVote(new LinkVote(link, user, VoteType.UPVOTE,
				"4.3.2.1"));
	}

	@Test
	public void createAndFindComment() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		Comment comment1 = panService.createComment(new Comment(link, user,
				"Excelente buscador!"));
		Comment comment2 = panService.findComment(comment1.getCommentId());

		assertEquals(comment2, comment1);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentComment() throws Exception {
		panService.findComment(NON_EXISTENT_ID);
	}

	@Test
	public void listCommentsByLinkId() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link1 = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));

		Links link2 = panService
				.createLink(new Links("http://www.yahoo.es", "Yahoo",
						"HomePage of Yahoo", "Google, search", user, category));
		panService
				.createComment(new Comment(link1, user, "Excelente buscador!"));
		panService.createComment(new Comment(link1, user, "Es de gran ayuda"));
		panService.createComment(new Comment(link2, user,
				"No es tan bueno como google"));

		ObjectBlock<FullComment> list = panService.listCommentsByLinkId(0, 10,
				link1.getLinkId(),null);

		assertEquals(list.getList().size(), 2);
	}

	@Test
	public void createAndFindCommentVote() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		Comment comment = panService.createComment(new Comment(link, user,
				"Excelente buscador!"));
		CommentVote commentVote1 = panService.createCommentVote(new CommentVote(comment, user, VoteType.UPVOTE));
		CommentVote commentVote2 = panService.findCommentVote(commentVote1.getVoteId());
		
		assertEquals(commentVote1, commentVote2);
	}
	
	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentCommentVote() throws Exception{
		panService.findCommentVote(NON_EXISTENT_ID);
	}
	
	@Test(expected = UserVotedException.class)
	public void createDuplicatedCommentVote() throws Exception {
		User user = panService.createUser(new User("Borja", "Borja",
				"borja@gmail.com", "192.168.1.1"));
		Category category = panService.createCategory(new Category(
				"Actualidad", null));
		Links link = panService.createLink(new Links("http://www.google.es",
				"Google", "HomePage of Google", "Google, search", user,
				category));
		Comment comment = panService.createComment(new Comment(link, user,
		"Excelente buscador!"));
		panService.createCommentVote(new CommentVote(comment, user, VoteType.UPVOTE));
		panService.createCommentVote(new CommentVote(comment, user, VoteType.DOWNVOTE));
	}
}
