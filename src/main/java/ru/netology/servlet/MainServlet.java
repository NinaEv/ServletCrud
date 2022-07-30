package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.exception.NotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PATH = "/api/posts";
    public static final String PATH_WITH_ARGUMENTS = PATH + "/";
    private PostController controller;

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        controller = context.getBean(PostController.class);
        final PostService service = context.getBean(PostService.class);
        final PostRepository repository = context.getBean(PostRepository.class);
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (method.equals(GET) && path.equals(PATH)) {
                controller.all(resp);
            } else if (method.equals(GET) && path.contains(PATH_WITH_ARGUMENTS)) {
                final var id = parse(path);
                controller.getById(id, resp);
            } else if (method.equals(POST) && path.equals(PATH)) {
                controller.save(req.getReader(), resp);
            } else if (method.equals(DELETE) && path.contains(PATH_WITH_ARGUMENTS)) {
                final var id = parse(path);
                controller.removeById(id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException | NumberFormatException exception) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    public long parse(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}

