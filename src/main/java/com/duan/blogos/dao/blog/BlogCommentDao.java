package com.duan.blogos.dao.blog;

import com.duan.blogos.dao.BaseDao;
import com.duan.blogos.entity.blog.BlogComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created on 2017/12/25.
 *
 * @author DuanJiaNing
 */
public interface BlogCommentDao extends BaseDao<BlogComment> {

    /**
     * 根据博文id查询评论
     *
     * @param blogId 博文id
     * @param status 博文状态
     * @param offset 偏移位置
     * @param rows   行数
     * @return
     */
    List<BlogComment> listCommentByBlogId(@Param("blogId") int blogId,
                                          @Param("offset") int offset,
                                          @Param("rows") int rows,
                                          @Param("status") int status);

}
