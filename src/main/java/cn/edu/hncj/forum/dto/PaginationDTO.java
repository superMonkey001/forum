package cn.edu.hncj.forum.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    // 当前的页数
    private Integer page;
    // 当前显示的所有页数
    private List<Integer> pages = new ArrayList<>();

    /**
     * @param totalCount 数据库中问题的总记录数
     * @param page       当前页
     * @param size       展示多少页
     */
    public void setPagination(Integer totalCount, Integer page, Integer size) {
        // 总页数
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        // 防止路径传入的page参数越界
        if(page < 1) {
            page = 1;
        }else if(page > totalPage) {
            page = totalPage;
        }
        this.page = page;

        pages.add(page);

        // 把前三页和后三页的的页码加入到pages中
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0,page - i);
            }

            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        // 是否展示上一页
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }

        // 是否展示下一页
        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }

        // 是否展示第一页
        if (pages.contains(1)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }

        // 是否展示最后一页
        if (pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}