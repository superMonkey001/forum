package cn.edu.hncj.forum.provider.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatar_url;
}
