package aaron.paper.common.exception;

import aaron.common.data.exception.NestedExamException;
import aaron.common.data.exception.StarterError;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
public class PaperException extends NestedExamException {

    public PaperException(String errorMessage, String errorCode) {
        super(errorMessage, errorCode);
    }
    public PaperException(StarterError error){ super(error.getMsg(),error.getCode());}
    public PaperException(PaperError error){
        super(error.msg,error.code);
    }
    public PaperException(PaperError error, Object...o){
        super(String.format(error.msg,o),error.code);
    }

}
