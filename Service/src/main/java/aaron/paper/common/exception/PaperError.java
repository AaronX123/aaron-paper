package aaron.paper.common.exception;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
public enum PaperError {
    PAPER_NOT_EXIST("020001","试卷不存在"),
    ;
    String code;
    String msg;

    PaperError(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
