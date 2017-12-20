package com.duan.blogos.web.audience;

import com.duan.blogos.common.Order;
import com.duan.blogos.common.Rule;
import com.duan.blogos.dto.blog.BlogListItemDTO;
import com.duan.blogos.entity.blogger.BloggerAccount;
import com.duan.blogos.exception.UnknownBloggerException;
import com.duan.blogos.manager.AudiencePropertiesManager;
import com.duan.blogos.manager.BlogSortRule;
import com.duan.blogos.result.ResultBean;
import com.duan.blogos.service.audience.BlogBrowseService;
import com.duan.blogos.service.audience.BlogRetrievalService;
import com.duan.blogos.service.blogger.BloggerAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created on 2017/12/19.
 *
 * @author DuanJiaNing
 */
@ControllerAdvice
@RequestMapping("/{bloggerName}")
public class BlogController {

    @Resource(name = "audiencePropertiesManager")
    private AudiencePropertiesManager audiencePropertiesManager;

    @Resource(name = "blogBrowseService")
    private BlogBrowseService browseService;

    @Resource(name = "blogRetrievalService")
    private BlogRetrievalService retrievalService;

    @Resource(name = "bloggerAccountService")
    private BloggerAccountService bloggerAccountService;

    @RequestMapping
    public String bloggerHomePage(@PathVariable("bloggerName")
                                  @ModelAttribute("bloggerName") String name) {
        checkAccount(name);
        return "/audience/blogger_home";
    }

    @RequestMapping("/blog")
    @ResponseBody
    public ResultBean<List<BlogListItemDTO>> bloggerBlogList(@PathVariable("bloggerName") String name,
                                                             @RequestParam(value = "cids", required = false) String categoryIds,
                                                             @RequestParam(value = "lids", required = false) String labelIds,
                                                             @RequestParam(value = "kword", required = false) String keyWord,
                                                             @RequestParam(value = "offset", required = false) Integer offset,
                                                             @RequestParam(value = "rows", required = false) Integer rows,
                                                             @RequestParam(value = "scode", required = false) String sort,
                                                             @RequestParam(value = "ocode", required = false) String order) {
        BloggerAccount account = checkAccount(name);

        int os = offset == null ? 0 : offset;
        int rs = rows == null ? audiencePropertiesManager.getDefaultRequestBloggerBlogListCount() : rows;
        BlogSortRule rule = new BlogSortRule(Rule.valueOf(sort == null ? "view_count" : sort), Order.valueOf(order == null ? "asc" : order));

        int[] cids = categoryIds == null ? null :
                Stream.of(categoryIds.split(","))
                        .mapToInt(Integer::valueOf)
                        .distinct()
                        .toArray();

        int[] lids = labelIds == null ? null :
                Stream.of(labelIds.split(","))
                        .mapToInt(Integer::valueOf)
                        .distinct()
                        .toArray();

        return retrievalService.listFilterAll(cids, lids, keyWord, account.getId(), os, rs, rule);
    }

    /*
     * 检查博主是否存在
     */
    private BloggerAccount checkAccount(String bloggerName) {
        BloggerAccount account = bloggerAccountService.getAccount(bloggerName);
        if (account == null) {
            throw new UnknownBloggerException();
        }
        return account;
    }

    @ExceptionHandler(UnknownBloggerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView exceptionHandler() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("404");
        mv.addObject("errorMsg", "博主不存在");
        return mv;
    }

}