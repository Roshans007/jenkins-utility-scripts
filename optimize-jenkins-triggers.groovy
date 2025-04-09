import com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger
import hudson.triggers.SCMTrigger
import jenkins.branch.MultiBranchProject

// Update all multibranch projects to index once per day
Jenkins.instance.getAllItems(MultiBranchProject.class).each { job ->
    def found = false
    job.getTriggers().each { key, trigger ->
        if (trigger instanceof PeriodicFolderTrigger) {
            def newTrigger = new PeriodicFolderTrigger("1d")
            job.removeTrigger(key)
            job.addTrigger(newTrigger)
            found = true
            println "Updated multibranch trigger to '1d' for: ${job.fullName}"
        }
    }
    if (found) job.save()
}

// Update SCM polling interval for seed jobs (ending with '_seed')
Jenkins.instance.getAllItems(hudson.model.Job.class).findAll { job ->
    job.fullName.toLowerCase().endsWith('_seed')
}.each { job ->
    def triggers = job.getTriggers()
    def updated = false

    triggers.each { key, trigger ->
        if (trigger instanceof SCMTrigger && trigger.spec == '* * * * *') {
            def newTrigger = new SCMTrigger("H * * * *")
            job.removeTrigger(key)
            job.addTrigger(newTrigger)
            updated = true
            println "Updated SCM trigger to 'H * * * *' for seed job: ${job.fullName}"
        }
    }
    if (updated) job.save()
}
