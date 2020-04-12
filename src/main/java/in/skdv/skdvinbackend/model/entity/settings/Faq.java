package in.skdv.skdvinbackend.model.entity.settings;

import javax.validation.constraints.NotNull;

public class Faq {

    @NotNull
    private int id;
    @NotNull
    private String question;
    @NotNull
    private String answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
