package com.buj.jenkins.bujsendplugin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import jenkins.tasks.SimpleBuildStep;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;


public class BujSendPlugin extends Recorder implements SimpleBuildStep {

    private final String url;
    private final String text;

    @DataBoundConstructor
    public BujSendPlugin(String url, String text) {
        this.url = url;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }


    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url);
            JsonObject json = new JsonObject();
            json.addProperty("text", text);
            String gson = new Gson().toJson(json);
            StringEntity entity = new StringEntity(gson);
            request.setEntity(entity);
            request.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
            taskListener.getLogger().println("resp :" + response);
        } catch (Exception e) {
            e.printStackTrace();
            taskListener.getLogger().println("err :" + e.getMessage());
        }
    }

    @Symbol("bujSend")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Buj Webhook Plugin";
        }
    }
}