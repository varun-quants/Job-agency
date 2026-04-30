package com.agency.ui;

import com.agency.model.*;
import com.agency.model.enums.JobStatus;
import com.agency.repository.impl.*;
import com.agency.service.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class AgencyGUI extends Application {

    //  Colors
    private static final String DARK = "#0D1B2A";
    private static final String SIDEBAR = "#1B2838";
    private static final String PANEL = "#F8F9FA";
    private static final String BORDER = "#E2E8F0";
    private static final String TMID = "#4A5568";
    private static final String TLIGHT = "#A0AEC0";
    private static final String PURPLE = "#6C63FF";
    private static final String CYAN = "#00B4D8";
    private static final String GREEN = "#38A169";
    private static final String RED = "#E53E3E";
    private static final String ORANGE ="#DD6B20";

    // Services
    private final FileSkillRepository skillRepo = new FileSkillRepository();
    private final FileJobRepository jobRepo = new FileJobRepository();
    private final FileJobseekerRepository skrRepo = new FileJobseekerRepository();
    private final FileJobSkillRepository jsRepo = new FileJobSkillRepository();
    private final FileSeekerSkillRepository ssRepo = new FileSeekerSkillRepository();

    private final SkillService SS = new SkillService(skillRepo);
    private final JobService JS = new JobService(jobRepo, jsRepo, skillRepo);
    private final JobseekerService SKS = new JobseekerService(skrRepo, ssRepo, skillRepo);
    private final MatchingService MS = new MatchingService(JS, SKS);


    // State
    private StackPane content;
    private Label statusLbl;
    private Button activeBtn;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setLeft(sidebar());
        root.setCenter(contentPane());
        root.setBottom(statusBar());
        show(dashboard());
        stage.setTitle("JobAgency Management System");
        stage.setScene(new Scene(root, 1100, 720));
        stage.setMinWidth(800);
        stage.setMinHeight(550);
        stage.show();
    }

    //layout
    private VBox sidebar() {
        VBox sb = new VBox();
        sb.setPrefWidth(210);
        sb.setStyle("-fx-background-color:" + SIDEBAR +";");

        VBox logo = new VBox(3);
        logo.setPadding(new Insets(24,18,20,18));
        logo.setStyle("-fx-background-color:" + DARK + ";");
        logo.getChildren().addAll(lbl("AGENCY",20,true,"#FFFFFF"), lbl("Management System",11,false,TLIGHT));

        Button d = navBtn("⬛ Dashboard", TLIGHT, null, () -> show(dashboard()));
        Button sk = navBtn("⬢ Skills", PURPLE, PURPLE, () -> show(skillsPanel()));
        Button jb = navBtn("⬢ Jobs", CYAN, CYAN, () -> show(jobsPanel()));
        Button se = navBtn("⬣ Seekers", GREEN, GREEN, () -> show(seekersPanel()));
        Button rp = navBtn("⬤ Reports", RED, RED, () -> show(reportsPanel()));
        setActive(d);

        Region sp = new Region();
        VBox.setVgrow(sp, Priority.ALWAYS);
        Label ver = lbl("v1.0 | CN5004", 10,false,TLIGHT);
        ver.setPadding(new Insets(14,18,14,18));

        sb.getChildren().addAll(logo, new Region() {{ setPrefHeight(4);}},
                padLbl("NAVIGATION", 10,TLIGHT, new Insets(16,18,6,18)),
                d,sk,jb,se,rp,sp,ver);
        return sb;
    }

    private ScrollPane contentPane() {
        content = new StackPane();
        content.setStyle("-fx-background-color:"+PANEL+";");
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true); sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:"+PANEL+";-fx-background:"+PANEL+";");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private HBox statusBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(5,14,5,14));
        bar.setStyle("-fx-background-color:"+DARK+";");
        statusLbl =lbl("Ready", 11,false,TLIGHT);
        statusLbl.setFont(Font.font("Monospaced", 11));
        bar.getChildren().add(statusLbl); return bar;
    }

    //SCREENS
    private VBox dashboard(){
        VBox pg = page("Dashboard","Agency overview", null);
        VBox b = body(pg);
        HBox cards = new HBox(14);
        cards.getChildren().addAll(
                statCard("Total Jobs", JS.getAllJobs().size(), CYAN),
                statCard("Open positions",JS.getOpenJobs().size(), GREEN),
                statCard("Seekers", SKS.getAllSeekers().size(), PURPLE),
                statCard("Skills", SS.getAllSkills().size(), ORANGE));
        b.getChildren().addAll(cards, secLbl("All Jobs"), jobTable(JS.getAllJobs()));
        st("Dashboard loaded", TLIGHT);
        return pg;
    }

    private VBox skillsPanel() {
        VBox pg = page("Skills","Manage the skill catalogue", PURPLE); VBox b = body(pg);
        TextField f = field("Skill name (e.g. Java, SQL)");
        Label fb = feedLbl();
        Button add = btn("Add Skill", PURPLE);
        add.setOnAction(e -> { try {
        Skill s = SS.createSkill(f.getText().trim()); f.clear();
        fb(fb,"✓ Created: ["+s.getId()+"] "+s.getName(), GREEN); show(skillsPanel());
        } catch (Exception ex) {
            fb(fb, "X " + ex.getMessage(), RED);
        }});

        VBox form = card();
        form.getChildren().addAll(lbl("Add New Skill",13,true, PURPLE),f,add,fb);

        TableView<Skill> t = new TableView<>(); t.setStyle(tSty()); stT(t);
        addCol(t,"ID",50,"id"); addCol(t, "Skill Name", 300,"name");
        TableColumn<Skill, Void> dc = new TableColumn<>("Action");
        dc.setPrefWidth(100);
        dc.setCellFactory(c -> delCell(() -> show(skillsPanel()),
                id -> SS.deleteSkill(id), r -> r.getId()));
        t.getColumns().add(dc);
        t.setItems(FXCollections.observableArrayList(SS.getAllSkills()));

        b.getChildren().addAll(form, secLbl("All Skills"), t);
        st(SS.getAllSkills().size()+ " skills", TLIGHT); return pg;
    }

    private VBox jobsPanel() {
        VBox pg = page("Jobs", "Manage job listings", CYAN); VBox b = body(pg);
        TextField ti = field("Title"), de = field("Description"),
                sa=field("Salary"), lo=field("Location"),
                sk=field("Skill IDs (e.g. 1,2,3)");
        Label fb = feedLbl();
        Button add = btn("Create Job", CYAN);
        add.setOnAction(e -> { try{
            Job j = JS.createJob(ti.getText().trim(), de.getText().trim(),
                    Double.parseDouble(sa.getText().trim()), lo.getText().trim(), ids(sk.getText()));
            fb(fb, "✓ Job ["+j.getId()+"] "+j.getTitle(), GREEN);
            ti.clear();
            de.clear();
            sa.clear();
            lo.clear();
            sk.clear();
            show(jobsPanel());
        } catch(Exception ex) { fb(fb, "X "+ex.getMessage(), RED);}
        });

        VBox form = card();
        form.getChildren().addAll(lbl("Add New Job",13,true,CYAN),
                ti,de,sa,lo,chips(CYAN),sk,add,fb);

        TableView<Job> t = jobTable(JS.getAllJobs());
        Button upd=btn("Update", ORANGE), sts=btn("Status", CYAN), del=btn("Delete", RED);
        upd.setOnAction(e -> {Job s=t.getSelectionModel().getSelectedItem();
            if(s==null){st("Select a job", ORANGE); return; } jobUpdateDlg(s);});
        sts.setOnAction(e -> {Job s=t.getSelectionModel().getSelectedItem();
            if(s==null){st("Select a job", ORANGE); return; } statusDlg(s);});
        del.setOnAction(e -> {Job s=t.getSelectionModel().getSelectedItem();
            if(s==null){st("Select a job", ORANGE); return; }
            if(ok("Delete job: "+s.getTitle()+"?"))
                try{ JS.deleteJob(s.getId()); show(jobsPanel());}
                catch(Exception ex) { st("Error: "+ex.getMessage(), RED);}
        });

        b.getChildren().addAll(form, secLbl("All Jobs"), t, row(upd,sts,del));
        st(JS.getAllJobs().size()+" jobs", TLIGHT);
        return pg;
    }

    private VBox seekersPanel() {
        VBox pg = page("Job Seekers", "Register and manage candidates", GREEN); VBox b = body(pg);

        TextField nm=field("Full Name"), em=field("Email"),
                ph=field("Phone"), lo=field("Location"),
                sk= field("Skill IDs (e.g. 1,2,3) ");

        Label fb = feedLbl();
        Button add= btn("Register Seeker", GREEN);
        add.setOnAction( e -> {
            try{
                Jobseeker s = SKS.registerSeeker(nm.getText().trim(), em.getText().trim(),
                        ph.getText().trim(), lo.getText().trim(), ids(sk.getText()));
                fb(fb,"✓ ["+s.getId()+"] "+s.getFullName(), GREEN);
                nm.clear();
                em.clear();
                ph.clear();
                lo.clear();
                sk.clear();
                show(seekersPanel());
            } catch(Exception ex) {
                fb(fb, "X "+ex.getMessage(),RED);
            }
        });

        VBox form = card();
        form.getChildren().addAll(lbl("Register New Seeker", 13,true,GREEN),
                nm,em,ph,lo,chips(GREEN),sk,add,fb);

        TableView<Jobseeker> t = seekerTable(SKS.getAllSeekers());
        Button upd=btn("Update", ORANGE), del=btn("Delete", RED);
        upd.setOnAction( e-> {
            Jobseeker s=t.getSelectionModel().getSelectedItem();
            if(s==null){st("Select a seeker", ORANGE);
                            return;
                        }
            seekerUpdateDlg(s);
        });
        del.setOnAction( e -> {
            Jobseeker s=t.getSelectionModel().getSelectedItem();
            if(s==null){st("Select a seeker", ORANGE); return;}
            if(ok("Delete "+s.getFullName()+"?"))
                try{ SKS.deleteSeeker(s.getId());
                show(seekersPanel());
                } catch(Exception ex){
                    st("Error: "+ex.getMessage(), RED);
                }
                }
        );

        b.getChildren().addAll(form, secLbl("Registered Seekers"),t,row(upd,del));
        st(SKS.getAllSeekers().size()+" seekers",TLIGHT);
        return pg;
    }

    private VBox reportsPanel(){
        VBox pg = page("Reports", "Matching and agency insights", RED);
        VBox b = body(pg);
        HBox cards = new HBox(12);
        cards.getChildren().addAll(
                rCard("Job -> Seekers", "Rank seekers for a job", RED, this::runJobMatch),
                rCard("Seekers -> Jobs", "Find jobs for a seeker", CYAN, this::runSeekerMatch),
                rCard("Unmatched Jobs", "Jobs with no qualifying seekers", ORANGE, this::runUnmatchedJobs),
                rCard("Unmatched Seekers", "Seekers with no matching jobs", PURPLE, this::runUnmatchedSeekers)
        );
        b.getChildren().addAll(secLbl("Sekect a Report"), cards);
        st("Reports ready", TLIGHT); return pg;
    }

    //REPORT RUNNERS

    private void runJobMatch() {
        List<Job> jobs = JS.getOpenJobs();
        if(jobs.isEmpty()) {st("No open jobs", ORANGE); return;}
        pick("Match Job -> Seekers",jobs.stream().map(j -> j.getId()+" - "+j.getTitle()).collect(Collectors.toList()),
                c -> {int id=Integer.parseInt(c.split(" - ")[0].trim());
                    List<MatchResult> r = MS.matchJobToSeekers(id);
                    VBox pg = page("Job -> Seekers", "Job ID: "+id, RED);
                    body(pg).getChildren().add(r.isEmpty()?empty("No matches above 50%"):matchTable(r, true));
                    show(pg); st(r.size()+ " matches", TLIGHT);
                }
                );
    }

    private void runSeekerMatch() {
        List<Jobseeker> sks = SKS.getAllSeekers();
        if(sks.isEmpty()) {
            st("No seekers registered", ORANGE); return;
        } pick("Match Seeker -> Jobs", sks.stream().map(s->s.getId()+" - "+s.getFullName()).collect(Collectors.toList()),
                c -> {
                    int id=Integer.parseInt(c.split(" - ")[0].trim());
                    List<MatchResult> r = MS.matchSeekerToJobs(id);
                    VBox pg = page("Seeker -> Jobs", "Seeker ID:"+id, CYAN);
                    body(pg).getChildren().add(r.isEmpty()?empty("No matches above 50%"): matchTable(r, false));
                    show(pg);
                    st(r.size()+" jobs matched",TLIGHT);
                });
    }

    private void runUnmatchedJobs(){
        List<Job> r = MS.findUnmatchedJobs();
        VBox pg = page("Unmatched jobs", r.size()+" unmatched",ORANGE);
        body(pg).getChildren().add(r.isEmpty()?empty("All jobs have qualifying seekers ✓"):jobTable(r));
        show(pg);
        st(r.size()+" unmatched jobs", TLIGHT);

    }

    private void runUnmatchedSeekers(){
        List<Jobseeker> r = MS.findUnmatchedSeekers();
        VBox pg = page("Unmatched Seekers", r.size()+" unmaatched", PURPLE);
        body(pg).getChildren().add(r.isEmpty()?empty("All seekers match atleast one job"):seekerTable(r));
        show(pg);
        st(r.size()+" unmatched seekers", TLIGHT);
    }

    //TABLES

    private TableView<Job> jobTable(List<Job> data) {
        TableView<Job> t = new TableView<>();
        t.setStyle(tSty());
        stT(t);
        addCol(t,"ID", 50,"id");
        addCol(t,"Title", 160,"title");
        addCol(t,"Location", 110,"location");
        addCol(t,"Salary",90,"salary");

        TableColumn<Job, String> stC = new TableColumn<>("Status");
        stC.setPrefWidth(85);
        stC.setCellValueFactory(d-> new SimpleStringProperty(d.getValue().getStatus().name()));
        stC.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if(empty || s==null){setText(null); setStyle(""); return;}
                setText(s);
                setStyle("-fx-text-fill:"+(s.equals("OPEN")?GREEN:s.equals("FILLED")?CYAN:RED)+";-fx-font-weight:bold;");
            }});

        TableColumn<Job, String> skC = new TableColumn<>("Required Skills");
        skC.setPrefWidth(200);
        skC.setCellValueFactory(d -> new SimpleStringProperty(
                JS.getJobSkills(d.getValue().getId()).stream().map(Skill::getName).collect(Collectors.joining(", "))
        ));

        t.getColumns().addAll(stC, skC);
        t.setItems(FXCollections.observableArrayList(data));
        return t;
    }

    private TableView<Jobseeker> seekerTable(List<Jobseeker> data) {
        TableView<Jobseeker> t = new TableView<>();
        t.setStyle(tSty());
        stT(t);
        addCol(t,"ID",50,"id");
        addCol(t,"Name",150,"fullName");
        addCol(t,"Email", 170, "email");
        addCol(t,"Location",110,"location");

        TableColumn<Jobseeker, String> skC = new TableColumn<>("Skills");
        skC.setPrefWidth(200);
        skC.setCellValueFactory(d -> new SimpleStringProperty(
                SKS.getSeekerSkills(d.getValue().getId()).stream().map(Skill::getName).collect(Collectors.joining(", "))
        ));

        t.getColumns().add(skC);
        t.setItems(FXCollections.observableArrayList(data));
        return t;
    }

    private TableView<MatchResult> matchTable(List<MatchResult> data, boolean jobMode){
        TableView<MatchResult> t = new TableView<>();
        t.setStyle(tSty());
        stT(t);
        TableColumn<MatchResult, String> rank = new TableColumn<>("#");
        rank.setPrefWidth(40);
        rank.setCellFactory(c -> new TableCell<>(){
            @Override
            protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty?null:String.valueOf(getIndex() + 1));
            }
        });

        TableColumn<MatchResult, String> name = new TableColumn<>(jobMode?"Candidate":"Job");
        name.setPrefWidth(180);
        name.setCellValueFactory(d -> new SimpleStringProperty(jobMode?d.getValue().getJobseeker().getFullName():d.getValue().getJob().getTitle()));

        TableColumn<MatchResult, String> sc = new TableColumn<>("Score");
        sc.setPrefWidth(80);
        sc.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.1f%%",d.getValue().getScore())));
        sc.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty){
                super.updateItem(s, empty);
                if(empty || s==null){setText(null); setStyle("");return;}
                setText(s);
                double v = Double.parseDouble(s.replace("%",""));
                setStyle("-fx-text-fill:"+(v>=100?GREEN:v>75?CYAN:ORANGE)+";-fx-font-weight:bold;");
            }
        });

        TableColumn<MatchResult, String> mt = new TableColumn<>("Matched");
        mt.setPrefWidth(190);
        mt.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getMatchedSkills().stream().map(Skill::getName).collect(Collectors.joining(", "))
        ));

        TableColumn<MatchResult, String> ms = new TableColumn<>("Missing");
        ms.setPrefWidth(190);
        mt.setCellValueFactory(d -> {String s = d.getValue().getMissingSkills().stream()
                .map(Skill::getName).collect(Collectors.joining(", "));
            return new SimpleStringProperty(s.isEmpty()?"None ✓":s);
        });
        mt.setCellFactory(c -> new TableCell<>(){
            @Override protected void updateItem(String s, boolean empty){
                super.updateItem(s,empty);
                if(empty || s ==null){setText(null); setStyle(""); return;}
                setText(s); setStyle("-fx-text-fill:"+(s.equals("None ✓")?GREEN:RED)+";");
            }
        });

        t.getColumns().addAll(rank,name,sc,mt,ms);
        t.setItems(FXCollections.observableArrayList(data));
        return t;
    }

    //DIALOGS
    private void jobUpdateDlg(Job job) {
        TextField ti = field("Title");
        ti.setText(job.getTitle());
        TextField de = field("Description");
        de.setText(job.getDescription());
        TextField sa = field("Salary");
        sa.setText(String.valueOf(job.getSalary()));
        TextField lo = field("Location");
        lo.setText(job.getLocation());

        dlg("Update Job", new VBox(10,ti,de,sa,lo), () -> {
            try{
                JS.updateJob(job.getId(), ti.getText().trim(), de.getText().trim(),
                        Double.parseDouble(sa.getText().trim()), lo.getText().trim()
                        );
                show(jobsPanel());
                st("Job updated", GREEN);
            } catch(Exception ex){
                st("Error: "+ex.getMessage(),RED);
            }
        });
    }

    private void statusDlg(Job job){
        ChoiceDialog<String> d = new ChoiceDialog<>(job.getStatus().name(),"OPEN","CLOSED","FILLED");
        d.setTitle("Change Status");
        d.setHeaderText("Job: "+job.getTitle());
        d.setContentText("New status: ");
        d.showAndWait().ifPresent(c -> { try {
            JS.updateJobStatus(job.getId(), JobStatus.valueOf(c));
            show(jobsPanel());
        } catch (Exception e){
            st("Error: "+e.getMessage(),RED);
        }
        });
    }

    private void seekerUpdateDlg(Jobseeker sk){
        TextField nm = field("Name");
        nm.setText(sk.getFullName());
        TextField em = field("Email");
        em.setText(sk.getEmail());
        TextField ph = field("Phone");
        ph.setText(sk.getPhone());
        TextField lo = field("Location");
        lo.setText(sk.getLocation());

        dlg("Update Seeker", new VBox(10,nm,em,ph,lo), () -> {
            try {
                SKS.updateSeeker(sk.getId(),nm.getText().trim(),em.getText().trim(),
                        ph.getText().trim(), lo.getText().trim());
                show(seekersPanel());
                st("Seeker updated", GREEN);
            } catch(Exception e){
                st("Error: "+e.getMessage(),RED);
            }});
    }

    private void dlg(String title, VBox content, Runnable onOk){
        content.setPadding(new Insets(16));
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(title);
        d.getDialogPane().setContent(content);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> onOk.run());
    }

    private void pick(String title, List<String> opts, Consumer<String> onPick) {
        ChoiceDialog<String> d = new ChoiceDialog<>(opts.get(0), opts);
        d.setTitle(title);
        d.setHeaderText("Select: ");
        d.setContentText("Choice: ");
        d.showAndWait().ifPresent(onPick);
    }

    private boolean ok(String msg){
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a.showAndWait().filter(b -> b ==ButtonType.YES).isPresent();
    }

    //COMPONENT BUILDERS

    private Button navBtn(String label, String color, String accent, Runnable action){
        Button b = new Button(label);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(11,18,11,18));
        b.setFont(Font.font("SansSerif",13));
        b.setTextFill(Color.web(color));
        b.setStyle("-fx-background-color:transparent; -fx-cursor:hand;");
        b.setOnAction(e -> {setActive(b); action.run();});
        b.setOnMouseEntered(e -> {if(b!=activeBtn)b.setStyle("-fx-background-color:rgba(255,255,255,0.07);-fx-cursor:hand;");});
        b.setOnMouseExited(e -> {if(b!=activeBtn)b.setStyle("-fx-background-color:transparent;-fx-cursor:hand;");});
        b.setUserData(accent);
        return b;
    }

    private void setActive(Button b){
        if(activeBtn!=null) activeBtn.setStyle("-fx-background-color:transparent;-fx-cursor:hand;");
        activeBtn=b;
        String ac=b.getUserData()!=null?b.getUserData().toString():"#FFFFFF";
        b.setStyle("-fx-background-color:rgba(255,255,255,0.10);-fx-border-color:"+ac+";-fx-border-width:0 0 0 3;-fx-cursor:hand;");
    }

    private VBox page(String title, String subtitle, String accent){
        VBox pg = new VBox(0);
        HBox hdr = new HBox();
        hdr.setPadding(new Insets(22,26,16,26));
        hdr.setStyle("-fx-background-color:"+PANEL+";-fx-border-color:"+BORDER+";-fx-border-width: 0 0 1 0;");
        hdr.getChildren().add(new VBox(3,
                lbl(title, 24, true, accent != null?accent:"#1A202C"),
                lbl(subtitle, 12, false, TMID)));
        VBox body = new VBox(14);
        body.setPadding(new Insets(18,26,26,26));
        body.setStyle("-fx-background-color:"+PANEL+";");
        VBox.setVgrow(body, Priority.ALWAYS);
        pg.getChildren().addAll(hdr, body);
        return pg;
    }

    private VBox body(VBox page){
        return (VBox)page.getChildren().get(1);
    }

    private VBox card() {
        VBox c = new VBox(10);
        c.setPadding(new Insets(14));
        c.setStyle("-fx-background-color:#FFFFFF;-fx-border-color:"+BORDER+";-fx-border-radius:7; -fx-background-radius:7;");
        return c;
    }

    private VBox statCard(String label, int value, String accent){
        VBox c = card();
        c.setPrefWidth(185);
        c.setAlignment(Pos.CENTER_LEFT);
        Label v = lbl(String.valueOf(value), 32,true,"#1A202C");
        v.setFont(Font.font("Monospaced", FontWeight.BOLD, 32));
        c.setStyle(c.getStyle()+"-fx-border-left-width:4; -fx-border-color:"+accent+";");
        c.getChildren().addAll(v, lbl(label, 12, false,TMID));
        HBox.setHgrow(c, Priority.ALWAYS);
        return c;

    }

    private VBox rCard(String title, String desc, String accent,Runnable action){
        VBox c = card();
        c.setPrefWidth(210);
        Button b = new Button("Run -> ");
        b.setStyle("-fx-background-color: "+accent+"22; -fx-text-fill: "+accent+";-fx-font-size:11;-fx-padding: 4 12;-fx-background-radius:4;-fx-cursor:hand;");
        b.setOnAction(e -> action.run());
        c.getChildren().addAll(lbl(title,13,true,"#1A202C"), lbl(desc,11,false,TMID), b);
        HBox.setHgrow(c, Priority.ALWAYS);
        return c;

    }

    private FlowPane chips(String color){
        FlowPane fp = new FlowPane(6,6);
        for(Skill s: SS.getAllSkills()){
            Label chip = new Label("["+s.getId()+"] "+s.getName());
            chip.setStyle("-fx-background-color:"+color+"22;-fx-text-fill:"+color+"-fx-background-radius:20; -fx-font-size:11;");
            fp.getChildren().add(chip);}
        return fp;
    }

    private <T> TableCell<T,Void> delCell(Runnable refresh, IntConsumer deleteAction, Function<T,Integer> getId) {
        return new TableCell<>() {
            final Button b = new Button("Delete");
            { b.setStyle("-fx-background-color:"+RED+";-fx-text-fill:white;-fx-font-size:11;-fx-padding:3 9;-fx-cursor:hand;-fx-background-radius:4;");
                b.setOnAction(e->{T item=getTableView().getItems().get(getIndex());
                    try{deleteAction.accept(getId.apply(item));refresh.run();}
                    catch(Exception ex){st("Error: "+ex.getMessage(),RED);}});}
            @Override protected void updateItem(Void v,boolean empty){super.updateItem(v,empty);setGraphic(empty?null:b);}
        };
    }

    private <T> void addCol(TableView<T> t, String name, double w, String prop) {
        TableColumn<T,?> c = new TableColumn<>(name);
        c.setCellValueFactory(new PropertyValueFactory<>(prop)); c.setPrefWidth(w);
        t.getColumns().add(c);
    }

    private HBox row(Button... buttons) {
        HBox h = new HBox(10); h.getChildren().addAll(buttons); return h;
    }

    private TextField field(String prompt) {
        TextField f = new TextField(); f.setPromptText(prompt);
        f.setStyle("-fx-background-color:"+PANEL+";-fx-border-color:"+BORDER+";-fx-border-radius:5;-fx-background-radius:5;-fx-padding:7 10;");
        return f;
    }

    private Button btn(String label, String color) {
        Button b = new Button(label); b.setFont(Font.font("SansSerif",FontWeight.BOLD,12));
        b.setStyle("-fx-background-color:"+color+";-fx-text-fill:white;-fx-padding:7 18;-fx-background-radius:5;-fx-cursor:hand;");
        return b;
    }

    private Label lbl(String t, int size, boolean bold, String color) {
        Label l = new Label(t);
        l.setFont(bold?Font.font("SansSerif",FontWeight.BOLD,size):Font.font("SansSerif",size));
        l.setTextFill(Color.web(color)); return l;
    }

    private Label padLbl(String t, int size, String color, Insets pad) {
        Label l = lbl(t,size,true,color); l.setPadding(pad); return l;
    }

    private Label secLbl(String t) { return padLbl(t.toUpperCase(),10,TLIGHT,new Insets(6,0,2,0)); }
    private Label feedLbl()        { Label l=new Label(); l.setFont(Font.font(12)); return l; }
    private Label empty(String t)  { Label l=lbl(t,13,false,TLIGHT); l.setPadding(new Insets(32)); return l; }

    private void show(VBox node)          { content.getChildren().setAll(node); StackPane.setAlignment(node,Pos.TOP_LEFT); }
    private void st(String msg,String c)  { statusLbl.setText(msg); statusLbl.setTextFill(Color.web(c)); }
    private void fb(Label l,String m,String c){ l.setText(m); l.setTextFill(Color.web(c)); }

    private String tSty() { return "-fx-background-color:#FFFFFF;-fx-border-color:"+BORDER+";-fx-border-radius:7;-fx-background-radius:7;"; }
    private <T> void stT(TableView<T> t) {
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); t.setFixedCellSize(36);
        t.setPrefHeight(Math.min(380,(t.getItems().size()+1.3)*36+8));
    }
    private List<Integer> ids(String raw) {
        List<Integer> ids = new ArrayList<>();
        for(String p:raw.split(",")){ String tr=p.trim(); if(!tr.isEmpty()) ids.add(Integer.parseInt(tr)); }
        return ids;
    }
}

