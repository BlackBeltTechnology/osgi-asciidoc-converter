# Development Version and Branch Handling

This document describes the branching strategy, versioning policy, and CI/CD pipeline for the OSGi AsciiDoc Converter project. The workflow is based on GitFlow and automated through GitHub Actions.

## Branches

The branching model follows [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow). Each branch type serves a specific purpose in the development lifecycle:

| Branch Pattern | Purpose | Based On |
|----------------|---------|----------|
| `develop` | Latest development sources of the active version | — |
| `feature/JNG-NUMBER_summary` | New features for the active version | `develop` |
| `(release/)X.Y.Z` | Release stabilization (`release/` prefix reserved for CI) | `develop` |
| `bugfix/JNG-NUMBER_summary` | Bug fixes during release testing | release branch |
| `support/JNG-NUMBER_summary` | Minor changes to a previous release | release branch |
| `master` | Latest released sources | release branch merge |
| `hotfix/JNG-NUMBER_summary` | Critical production fixes | `master` |

> **Important:** Bugfix and support branches must be applied to **both** the originating release branch and all newer release/development branches.

```mermaid
gitGraph
    commit id: "initial"
    branch develop
    commit id: "dev-1"
    branch feature/JNG-1
    commit id: "feat-1"
    commit id: "feat-2"
    checkout develop
    merge feature/JNG-1 id: "merge-feat-1"
    branch feature/JNG-3
    commit id: "feat-3"
    checkout develop
    merge feature/JNG-3 id: "merge-feat-3"
    branch release/1.0-beta1
    commit id: "rc-1"
    branch bugfix/JNG-4
    commit id: "bugfix"
    checkout release/1.0-beta1
    merge bugfix/JNG-4 id: "merge-bugfix"
    checkout develop
    merge release/1.0-beta1 id: "merge-release"
    checkout main
    merge release/1.0-beta1 id: "release-1.0"
```

## Version Numbers

Versions follow semantic versioning. The rules for when to increment each part depend on the branch type:

| Branch Type | Version Action |
|-------------|---------------|
| `feature/*` | Do **not** change version numbers |
| `develop` | Increment 2nd number when a release branch is created |
| `bugfix/*` | Do **not** change version numbers (applied to release branches pre-merge to master) |
| `support/*` | Increment 3rd number when started (for minor changes to previous releases) |
| `hotfix/*` | Increment 4th number when started (applied to both release and master branches) |

## GitHub Actions Workflows

The CI/CD system uses four interconnected GitHub Actions workflows. Each workflow triggers automatically based on branch and tag events.

### build.yml — Main Build Pipeline

This workflow runs on every push to `develop` and on pull requests targeting `develop`, `master`, `increment/*`, or `release/*` branches.

```mermaid
flowchart TD
    A["Push on develop or PR on<br/>develop / master / increment / release"] --> B{Branch type?}
    B -->|master, release/*| C["Version from pom.xml<br/>(without -SNAPSHOT)"]
    B -->|develop, increment/*| D["Version: major.minor.qualifier<br/>.date_commitId_branchName"]
    C --> E["Build and deploy to Nexus"]
    D --> E
    E --> F["Create git tag v&lt;version&gt;"]
    F --> G{Branch type?}
    G -->|increment/*, release/*| H["Create tag merge-pr/&lt;version&gt;"]
    H --> I["Triggers merge-pr-tagged.yml"]
    G -->|develop| J["Build changelog"]
    J --> K["Create GitHub pre-release"]
```

### merge-pr-tagged.yml — PR Auto-Merge

Triggered when a `merge-pr/*` tag is pushed. Routes the PR to the correct target branch based on version format.

```mermaid
flowchart TD
    A["Push on merge-pr/* tag"] --> B["Extract version from tag"]
    B --> C{Version format?}
    C -->|"major.minor.qualifier<br/>(release version)"| D["Merge PR to master"]
    D --> E["Triggers create-release-on-master.yml"]
    C -->|"Other format<br/>(development version)"| F["Squash PR to develop"]
    F --> G["Triggers build.yml"]
    D --> H["Delete merge-pr tag"]
    F --> H
```

### create-release-on-master.yml — Release Finalization

Triggered on every push to `master`. Creates the final GitHub release with a generated changelog.

```mermaid
flowchart TD
    A["Push on master"] --> B["Get version from tag"]
    B --> C["Build changelog"]
    C --> D["Create GitHub release (latest)"]
```

### release.yml — Manual Release Trigger

Manually triggered with a version parameter. Creates pull requests for both the release and the next development version.

```mermaid
flowchart TD
    A["Manual trigger with version"] --> B{Version input?}
    B -->|'auto'| C["Read version from pom.xml<br/>(without -SNAPSHOT)"]
    B -->|"Specific version<br/>(major.minor.qualifier)"| D["Use given version"]
    C --> E["Set next version = qualifier + 1"]
    D --> E
    E --> F["Create PR on master<br/>with release version"]
    E --> G["Create PR on develop<br/>with next version"]
    F --> H["Triggers build.yml"]
    G --> H
```

## How to Develop

Issue tracking uses [JIRA](https://blackbelt.atlassian.net/jira/dashboards).

> **Important:** There is no commit without a ticket number. Every pull request and commit must include a `JNG-xxx` reference.
