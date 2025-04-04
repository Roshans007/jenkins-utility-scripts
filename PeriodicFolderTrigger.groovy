import com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger
import jenkins.branch.MultiBranchProject

def updated = 0

Jenkins.instance.getAllItems(MultiBranchProject.class).each { job ->
    def currentTrigger = job.getTriggers()[PeriodicFolderTrigger]

    if (currentTrigger != null) {
        if (currentTrigger.getInterval() != 3600000) {
            job.removeTrigger(currentTrigger)
            println "Removed old trigger for: ${job.fullName}"
        } else {
            // Already set to 1 hour, skip
            return
        }
    }

    def newTrigger = new PeriodicFolderTrigger("3600000") // 1 hour
    newTrigger.start(job, true)
    job.addTrigger(newTrigger)
    job.save()
    println "✅ Set 1h PeriodicFolderTrigger for: ${job.fullName}"
    updated++
}

println "\n✅ Updated triggers for ${updated} multibranch jobs."
