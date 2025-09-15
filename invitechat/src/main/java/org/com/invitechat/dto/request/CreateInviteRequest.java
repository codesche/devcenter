package org.com.invitechat.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * 초대 링크 생성 시 TTL(초)을 입력받는다.
 * - 너무 짧거나 긴 값은 방어적으로 제한한다.
 */

@Getter
@Setter
public class CreateInviteRequest {

    @Min(60)
    @Max(86400)
    private Integer ttlSeconds = 1800;

}
