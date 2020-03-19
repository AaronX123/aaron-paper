package aaron.paper.common.exception;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
public enum PaperError {
    PAPER_NOT_EXIST("020001","试卷不存在"),
    PAPER_SUBJECT_CANT_BE_NULL("020002","试题不能为空"),
    PAPER_CANT_BE_NULL("020003","试卷不能为空"),
    PAPER_INSERT_FAILURE("020004","试卷插入失败"),
    PAPER_SUBJECT_INSERT_FAILURE("020005","试题插入失败"),
    PAPER_SUBJECT_ANSWER_INSERT_FAILURE("020006","试题答案插入失败"),
    PAPER_DELETE_FAILURE("020007","试卷删除失败"),
    PAPER_SUBJECT_IS_NULL("020008","试卷没有试题"),
    PAPER_REPEATED_PAPER("020009","重复的试卷"),
    ;
    String code;
    String msg;

    PaperError(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
