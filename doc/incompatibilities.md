# 5.2.1

1. User by default is not able to create/fork projects now. To allow it, specify default login group with appropriate project create permissions in security setting.
2. URL of project is changed from *http(s)://\<onedev-server\>/projects/\<project name\>* to *http(s)://\<onedev-server\>/projects/\<project id\>*. This only affects web UI, clone url and REStful api url is not changed.
3. Job variable *@project_name@* should be replaced by *@project_path@*.
4. Job match condition in job executor is renamed as job requirement, and the criteria to match job name is no longer valid. Executor can now be specified when define the job.

# 4.3.0

1. Service definition is separated from job definition. Previous service defined in job will be moved out of job and take the name _\<job name\>-\<service name\>_ to avoid possible name conflicts. You either need to change it back to use original name in case there is no conflicts, or change your build script to use the new service name. 
2. Job match condition of job executor no longer accepts image criteria. You need to remove it manually if there is any; otherwise the job matching will fail.

# 4.1.3

1. Html report is removed from build spec due to possible XSS vulnerabilities. Check issue #230 for details
2. Setting _Default Fixed Issues Filter_ in build spec has been moved to be under project build setting, in order to facilitate issue query auto-updating upon custom field/state change

# 4.1.0

1. Backslash in job commands should not be escaped now. And literal '@' should be written as '@@'
2. Various query operator _is before_ and _is after_ is substituted with _is until_ and _is since_. For instance issue query _"Submit Date" is before "yesterday"_ should be written as _"Submit Date" is until "yesterday"_

# 4.0.5

1. Renamed build variables:

  |old name|new name|
  |---|---|
  |updated_ref|ref|
  |updated_branch|branch|
  |updated_tag|tag|
  |pull_request_ids|pull_request_number|

1. Removed build variables: _on_branches_, _commit_tags_, 

# 3.2

Version 3.2 uses token authentication instead of password authentication in build spec for security 
consideration. As a result of this, password authentication specified previously will be cleared:

1. Submodule authentication
  
  You will need to use custom http/ssh clone credential with permission to access submodule projects to retrieve source. Refer to [usage scenario](https://code.onedev.io/projects/onedev-manual/blob/master/pages/clone-submodules-via-ssh.md) for an example.
  
2. Project dependency authentication

  You will need to define a job secret containing an access token in project build setting page, and then use that secret in project dependency definition. The access token should have permission to download dependency project artifacts of course.