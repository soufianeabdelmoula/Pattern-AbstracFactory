package fr.vdm.referentiel.refadmin.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GenericPaginationContainer<T> {
    private Long xTotalCount;
    private Integer xTotalPages;
    private T content;

    public GenericPaginationContainer(Long xTotalCount, Integer xTotalPages, T content) {
        this.xTotalCount = xTotalCount;
        this.xTotalPages = xTotalPages;
        this.content = content;
    }


    public ResponseEntity<T> getResponseEntity() {
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.set("X-Total-Count", this.xTotalCount.toString());
        responseHeaders.set("X-Total-Pages", this.xTotalPages.toString());
        responseHeaders.set("Access-Control-Expose-Headers", "X-Total-Count,X-Total-Pages");

        return new ResponseEntity(this.content, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    }

    public Long getxTotalCount() {
        /* 34 */
        return this.xTotalCount;
    }

    public void setxTotalCount(Long xTotalCount) {
        /* 38 */
        this.xTotalCount = xTotalCount;
    }

    public Integer getxTotalPages() {
        /* 42 */
        return this.xTotalPages;
    }

    public void setxTotalPages(Integer xTotalPages) {
        /* 46 */
        this.xTotalPages = xTotalPages;
    }

    public T getContent() {
        /* 50 */
        return this.content;
    }

    public void setContent(T content) {
        /* 54 */
        this.content = content;
    }
}
