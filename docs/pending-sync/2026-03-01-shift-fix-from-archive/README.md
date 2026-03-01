# Pending Sync: Shift Break Fix (from archive source)

Source repository:
`C:\Users\ASUS\Documents\servers\_archive\servercreative-20260228-220814\source\opencreative-src`

Source commit:
`756ff10` (`fix(coding): shift remove nested branch bounds and safe gap compaction`)

What is included:
- `DestroyBlockListener.from-archive.java` - exact source snapshot from commit.
- `shift-fix.patch` - git patch for targeted cherry-pick/manual apply.

Reason for pending sync:
- Active repo (`C:\Users\ASUS\Documents\servercreative\source\opencreative-src`) and archive repo diverged.
- Requested safe transfer without overwriting existing files.

Recommended apply flow in active repo:
1. Review `shift-fix.patch`.
2. Apply patch manually to `src/main/java/ua/mcchickenstudio/opencreative/listeners/player/DestroyBlockListener.java`.
3. Build: `mvn -DskipTests package`.
4. Deploy and verify nested case: `if [ if [ if [ ] ] ]` with shift-break on inner blocks.
