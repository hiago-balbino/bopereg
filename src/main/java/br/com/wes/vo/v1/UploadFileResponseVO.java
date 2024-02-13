package br.com.wes.vo.v1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8824301687693002293L;

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

}
