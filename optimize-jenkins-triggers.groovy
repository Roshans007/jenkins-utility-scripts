import com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger
import hudson.triggers.SCMTrigger
import jenkins.branch.MultiBranchProject

// === Update multibranch projects with '1m' indexing to '1d' ===
Jenkins.instance.getAllItems(MultiBranchProject.class).each { job ->
    def triggers = job.getTriggers()
    def updated = false

    triggers.each { key, trigger ->
        if (trigger instanceof PeriodicFolderTrigger && trigger.getInterval() == "1m") {
            def newTrigger = new PeriodicFolderTrigger("1d")
            job.removeTrigger(key)
            job.addTrigger(newTrigger)
            updated = true
            println "Updated multibranch indexing from '1m' to '1d' for: ${job.fullName}"
        }
    }

    if (updated) job.save()
}

// === Update seed jobs' SCM polling from every minute to hourly ===
Jenkins.instance.getAllItems(hudson.model.Job.class).findAll { job ->
    job.fullName.toLowerCase().endsWith('_seed')
}.each { job ->
    def updated = false
    job.getTriggers().each { key, trigger ->
        if (trigger instanceof SCMTrigger && trigger.spec.trim() == '* * * * *') {
            def newTrigger = new SCMTrigger("H * * * *")
            job.removeTrigger(key)
            job.addTrigger(newTrigger)
            updated = true
            println "Updated SCM polling to hourly for seed job: ${job.fullName}"
        }
    }

    if (updated) job.save()
}
