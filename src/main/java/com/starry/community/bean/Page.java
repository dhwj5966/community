package com.starry.community.bean;

/**
 * @author Starry
 * @create 2022-09-02-9:42 PM
 * @Describe  用于分页的类
 */
public class Page {
    //当前页码
    private int current = 1;
    // 该页的数据数
    private int limit = 10;
    //数据总数，用来计算总页数，显示在分页栏
    private int rows;
    //查询路径（用于复用分页连接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset() {
        //  (current - 1) * limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        }
        return rows / limit + 1;
    }

    /**
     *
     * @return 当前页的分页条的前置页的起始页号
     */
    public int getFrom() {
        if (current >= 5) {
            return current - 4;
        } else {
            return 1;
        }
    }

    /**
     *
     * @return 当前页的分页条的后置页的结尾页号
     */
    public int getTo() {
        if (current <= getTotal() - 4) {
            return current + 4;
        } else {
            return getTotal();
        }
    }
}
