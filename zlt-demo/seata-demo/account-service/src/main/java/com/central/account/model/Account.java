package com.central.account.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 账号
 *
 * @author zlt
 * @date 2019/9/14
 */
@Data
@Accessors(chain = true)
@TableName("account_tbl")
public class Account {
  @TableId
  private Long id;
  private String userId;
  private Integer money;
}
