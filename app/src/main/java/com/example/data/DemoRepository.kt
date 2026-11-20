package com.example.data

object DemoRepository {

    val anonymousAliases = listOf(
        "Quiet Sprout",
        "Mossy Owl",
        "Warm Chamomile",
        "Gentle Fern",
        "Morning Pine",
        "Soft Meadow"
    )

    val guidelines = listOf(
        GuidelineItem(
            id = "respect",
            title = "Respect & Kindness",
            subtitle = "Lead with warmth and curiosity",
            description = "Speak to others the way you wish to be spoken to on a tender day. Disagree gently, avoid judgment, and honor every person's pace.",
            iconKey = "favorite"
        ),
        GuidelineItem(
            id = "safety",
            title = "Emotional Safety First",
            subtitle = "Keep spaces grounded and gentle",
            description = "Avoid graphic descriptions of self-harm or harsh language. Use content notes when sharing heavy feelings so peers can read at their own comfort level.",
            iconKey = "shield"
        ),
        GuidelineItem(
            id = "support",
            title = "Peer Support, Not Clinical Care",
            subtitle = "Shared stories, never medical advice",
            description = "NeuroTalks is a reflective wellness prototype for peer encouragement. Community notes and audio reflections are not therapy, diagnosis, or clinical counseling.",
            iconKey = "handshake"
        ),
        GuidelineItem(
            id = "confidentiality",
            title = "Confidentiality & Anonymity",
            subtitle = "Protect your privacy and others'",
            description = "Never share real full names, phone numbers, home addresses, or identifying workplace details. This demo uses local anonymous aliases only.",
            iconKey = "lock"
        ),
        GuidelineItem(
            id = "reporting",
            title = "Reporting & Community Care",
            subtitle = "Flag anything that feels unsafe",
            description = "If a post feels unkind, intrusive, or worrying, tap the report icon on any card. In this local prototype, reporting hides the post immediately for your preview.",
            iconKey = "flag"
        ),
        GuidelineItem(
            id = "emergency",
            title = "Emergency & Crisis Resources",
            subtitle = "Immediate human help is always one tap away",
            description = "If you or someone nearby feels in immediate danger or deep crisis, please reach out to verified crisis hotlines or local emergency services right away.",
            iconKey = "emergency"
        )
    )

    val podcastTopics = listOf(
        "All Topics",
        "Anxiety & Calm",
        "Boundaries",
        "Burnout Recovery",
        "Self-Compassion",
        "Evening Wind-Down"
    )

    val podcastEpisodes = listOf(
        PodcastEpisode(
            id = "ep_1",
            title = "Unclenching Your Shoulders at 3 PM",
            host = "Dr. Lin (Demo Host) & Maya",
            topic = "Anxiety & Calm",
            style = PodcastStyle.GUIDED_AUDIO,
            durationMinutes = 8,
            durationSeconds = 480,
            summary = "A soft mid-afternoon pause designed to notice hidden physical tension, slow your exhale, and reset racing thoughts without forcing productivity.",
            takeaways = listOf(
                "Drop your shoulders and soften your jaw before solving the next task.",
                "A slightly longer exhale signals calm to your nervous system.",
                "Micro-pauses of 90 seconds can prevent end-of-day exhaustion."
            ),
            reflectionPrompt = "Where in your body are you holding today's to-do list?"
        ),
        PodcastEpisode(
            id = "ep_2",
            title = "Saying No Without Writing a Three-Page Apology",
            host = "Samira & Theo",
            topic = "Boundaries",
            style = PodcastStyle.FIRESIDE_CHAT,
            durationMinutes = 14,
            durationSeconds = 840,
            summary = "An honest, friendly chat about people-pleasing habits, gentle scripts for declining extra commitments, and sitting comfortably with small discomfort.",
            takeaways = listOf(
                "Warmth and firmness can live in the same sentence.",
                "Try: 'I appreciate you thinking of me, and I don't have capacity this week.'",
                "Clear boundaries protect future kindness toward yourself and others."
            ),
            reflectionPrompt = "What is one small boundary that would give you breathing room this week?"
        ),
        PodcastEpisode(
            id = "ep_3",
            title = "Rebuilding Energy When Rest Doesn't Feel Restful",
            host = "Nadia K.",
            topic = "Burnout Recovery",
            style = PodcastStyle.GENTLE_INTERVIEW,
            durationMinutes = 18,
            durationSeconds = 1080,
            summary = "Why scrolling in bed rarely restores a tired mind, and how playful, low-stakes sensory rest helps rebuild curiosity after long stretches of stress.",
            takeaways = listOf(
                "Distinguish between physical rest, sensory quiet, and emotional relief.",
                "Low-pressure hobbies without metrics recharge creative energy.",
                "Recovery happens in small repeatable rhythms, not one weekend."
            ),
            reflectionPrompt = "What low-effort activity leaves you feeling slightly lighter afterward?"
        ),
        PodcastEpisode(
            id = "ep_4",
            title = "Talking to Your Inner Critic Like an Old Friend",
            host = "Elena Moss",
            topic = "Self-Compassion",
            style = PodcastStyle.SOLO_REFLECTION,
            durationMinutes = 11,
            durationSeconds = 660,
            summary = "A warm solo reflection on noticing perfectionist inner commentary and gently shifting toward a steadier, kinder internal narrator.",
            takeaways = listOf(
                "Name the critical voice softly: 'My perfectionist mind is worried right now.'",
                "Self-kindness improves resilience more than harsh self-pressure.",
                "Progress counts even when it looks quiet or unfinished."
            ),
            reflectionPrompt = "What would you say to a close friend experiencing your exact week?"
        ),
        PodcastEpisode(
            id = "ep_5",
            title = "Closing the Tabs in Your Mind Before Sleep",
            host = "Owen Park",
            topic = "Evening Wind-Down",
            style = PodcastStyle.GUIDED_AUDIO,
            durationMinutes = 12,
            durationSeconds = 720,
            summary = "An unhurried evening audio ritual to park unfinished worries on paper and ease into quiet sleep.",
            takeaways = listOf(
                "Write tomorrow's top two items on a note so your brain can stop rehearsing them.",
                "Dim lights 30 minutes before rest to cue your natural wind-down.",
                "Notice three quiet ordinary moments that went okay today."
            ),
            reflectionPrompt = "What thought can wait patiently until tomorrow morning?"
        )
    )

    val simulationCharacters = listOf(
        SimulationCharacter(
            id = "maya",
            name = "Maya",
            role = "Creative Restorer",
            vibe = "Warm & Reflective",
            bio = "Balancing a busy design schedule while learning to protect slow, nourishing mornings.",
            initials = "MY"
        ),
        SimulationCharacter(
            id = "leo",
            name = "Leo",
            role = "Mindful Student",
            vibe = "Curious & Steady",
            bio = "Navigating exams, social energy, and comparison with grounded daily routines.",
            initials = "LE"
        ),
        SimulationCharacter(
            id = "sam",
            name = "Sam",
            role = "Career Pivoter",
            vibe = "Thoughtful & Brave",
            bio = "Stepping away from chronic overwork to build a healthier definition of success.",
            initials = "SA"
        ),
        SimulationCharacter(
            id = "ari",
            name = "Ari",
            role = "Calm Connector",
            vibe = "Kind & Open",
            bio = "Practicing honest boundaries while nurturing deep, supportive friendships.",
            initials = "AR"
        )
    )

    val simulationGoals = listOf(
        SimulationGoal(
            id = "morning_rhythm",
            title = "Peaceful Morning Rhythm",
            subtitle = "Start the day grounded before messages and hurry take over",
            focusTag = "Calm & Focus"
        ),
        SimulationGoal(
            id = "work_boundaries",
            title = "Healthy Everyday Boundaries",
            subtitle = "Respond to pressure with calm clarity instead of automatic yeses",
            focusTag = "Clarity & Energy"
        ),
        SimulationGoal(
            id = "burnout_restore",
            title = "Restoring Energy After Burnout",
            subtitle = "Choose sustainable pacing when your battery feels nearly empty",
            focusTag = "Energy & Kindness"
        ),
        SimulationGoal(
            id = "deep_connection",
            title = "Nurturing Real Connection",
            subtitle = "Reach out with authenticity even when social anxiety whispers",
            focusTag = "Connection & Warmth"
        )
    )

    fun getScenarioStages(characterName: String, goalId: String): List<SimulationStage> {
        return when (goalId) {
            "work_boundaries" -> listOf(
                SimulationStage(
                    stageNumber = 1,
                    timeOfDay = "9:15 AM • Soft Morning Light",
                    title = "An Urgent Extra Request Arrives",
                    narrative = "$characterName opens their inbox to a message asking to take on an unplanned task by tonight, even though the day is already full.",
                    choices = listOf(
                        SimulationChoice(
                            id = "wb_1a",
                            label = "Pause for three breaths, then reply with a clear timeline for Thursday",
                            outcomeText = "Taking a breath broke the urgency spell. The clear alternative felt respectful and protected your focus.",
                            calmDelta = 12, clarityDelta = 15, connectionDelta = 6, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "wb_1b",
                            label = "Ask which existing priority can be moved if this needs to happen today",
                            outcomeText = "Collaborative boundary-setting! You invited shared problem-solving instead of absorbing all the stress alone.",
                            calmDelta = 8, clarityDelta = 18, connectionDelta = 10, energyDelta = 8
                        ),
                        SimulationChoice(
                            id = "wb_1c",
                            label = "Say yes immediately and skip lunch to squeeze it in",
                            outcomeText = "You got quick approval, but tension crept into your shoulders. Notice how automatic yeses quietly drain energy.",
                            calmDelta = -10, clarityDelta = -4, connectionDelta = 2, energyDelta = -14
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 2,
                    timeOfDay = "1:00 PM • Midday Crossroads",
                    title = "Guilt Whispers During Your Break",
                    narrative = "A quiet lunch break arrives, yet $characterName feels a pull to check notifications 'just in case' someone needs something.",
                    choices = listOf(
                        SimulationChoice(
                            id = "wb_2a",
                            label = "Step outside for 10 minutes with phone on Do Not Disturb",
                            outcomeText = "Fresh air and green leaves softened the mental buzz. Returning afterward felt noticeably sharper.",
                            calmDelta = 14, clarityDelta = 10, connectionDelta = 4, energyDelta = 14
                        ),
                        SimulationChoice(
                            id = "wb_2b",
                            label = "Message a trusted friend for a lighthearted five-minute check-in",
                            outcomeText = "A quick laugh reminded you that your identity is far bigger than your task list.",
                            calmDelta = 8, clarityDelta = 6, connectionDelta = 16, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "wb_2c",
                            label = "Keep scrolling messages while eating at the desk",
                            outcomeText = "The afternoon felt blurry and longer. Real breaks need a small visual separation from work.",
                            calmDelta = -8, clarityDelta = -6, connectionDelta = -4, energyDelta = -10
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 3,
                    timeOfDay = "6:05 PM • Closing the Day",
                    title = "Designing Your Evening Transition",
                    narrative = "One small task remains unfinished. $characterName decides how to step from work mode into personal evening peace.",
                    choices = listOf(
                        SimulationChoice(
                            id = "wb_3a",
                            label = "Write a 2-line handoff note to tomorrow-self and close the workspace",
                            outcomeText = "Parking the task on paper quieted your mind. Tomorrow-you has a clear starting point, and tonight is yours.",
                            calmDelta = 15, clarityDelta = 14, connectionDelta = 8, energyDelta = 12
                        ),
                        SimulationChoice(
                            id = "wb_3b",
                            label = "Put on a NeuroTalks wind-down audio episode while making tea",
                            outcomeText = "Turning the transition into a cozy sensory ritual helped your nervous system truly clock out.",
                            calmDelta = 16, clarityDelta = 8, connectionDelta = 6, energyDelta = 14
                        ),
                        SimulationChoice(
                            id = "wb_3c",
                            label = "Stay online another hour to chase perfection",
                            outcomeText = "You finished one detail, yet evening rest shrank. Practicing 'good enough for today' takes gentle repetition.",
                            calmDelta = -6, clarityDelta = 2, connectionDelta = -8, energyDelta = -12
                        )
                    )
                )
            )

            "burnout_restore" -> listOf(
                SimulationStage(
                    stageNumber = 1,
                    timeOfDay = "10:00 AM • Slow Saturday",
                    title = "Waking Up Still Tired",
                    narrative = "$characterName wakes up with low energy and sees a long mental list of chores, errands, and unanswered messages.",
                    choices = listOf(
                        SimulationChoice(
                            id = "br_1a",
                            label = "Pick just ONE gentle priority and move the rest to 'maybe later'",
                            outcomeText = "Shrinking the list immediately lifted the heavy fog. Progress feels kind when expectations match your actual battery.",
                            calmDelta = 14, clarityDelta = 14, connectionDelta = 4, energyDelta = 12
                        ),
                        SimulationChoice(
                            id = "br_1b",
                            label = "Sit by a window with warm tea for 10 quiet minutes first",
                            outcomeText = "Meeting yourself with warmth before making demands set a restorative tone for the entire day.",
                            calmDelta = 16, clarityDelta = 8, connectionDelta = 4, energyDelta = 14
                        ),
                        SimulationChoice(
                            id = "br_1c",
                            label = "Force yourself to sprint through every chore at once",
                            outcomeText = "Pushing through an empty tank left you feeling irritable by noon. Your body was asking for pacing, not punishment.",
                            calmDelta = -10, clarityDelta = -4, connectionDelta = -6, energyDelta = -16
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 2,
                    timeOfDay = "2:30 PM • Afternoon Slump",
                    title = "Choosing Rest That Actually Recharges",
                    narrative = "Energy dips again. $characterName notices the habit of doom-scrolling on the couch even though it never truly helps.",
                    choices = listOf(
                        SimulationChoice(
                            id = "br_2a",
                            label = "Take a slow, phone-free walk around the block noticing 3 green things",
                            outcomeText = "Gentle movement and natural light refreshed your sensory system far better than a screen.",
                            calmDelta = 12, clarityDelta = 12, connectionDelta = 6, energyDelta = 16
                        ),
                        SimulationChoice(
                            id = "br_2b",
                            label = "Do 15 minutes of a playful, zero-pressure hobby (doodling or music)",
                            outcomeText = "Creating something without grades or deadlines sparked a tiny, welcome flicker of joy.",
                            calmDelta = 10, clarityDelta = 10, connectionDelta = 8, energyDelta = 14
                        ),
                        SimulationChoice(
                            id = "br_2c",
                            label = "Scroll social feeds comparing your rest day to others' highlight reels",
                            outcomeText = "Comparison quietly drained the rest you needed. Noticing the pattern is the first step to changing it.",
                            calmDelta = -10, clarityDelta = -8, connectionDelta = -8, energyDelta = -10
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 3,
                    timeOfDay = "8:30 PM • Soft Landing",
                    title = "Honoring Small Progress",
                    narrative = "Before bed, $characterName reflects on the day and decides how to speak to themselves about resting.",
                    choices = listOf(
                        SimulationChoice(
                            id = "br_3a",
                            label = "Thank yourself for listening to your limits today",
                            outcomeText = "Self-compassion turned rest from guilt into genuine nourishment.",
                            calmDelta = 16, clarityDelta = 12, connectionDelta = 10, energyDelta = 14
                        ),
                        SimulationChoice(
                            id = "br_3b",
                            label = "Text a supportive friend: 'Taking a quiet recharge weekend, thinking of you'",
                            outcomeText = "Being honest about needing rest deepened trust without overextending yourself.",
                            calmDelta = 10, clarityDelta = 8, connectionDelta = 16, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "br_3c",
                            label = "Criticize yourself for not accomplishing enough",
                            outcomeText = "Harsh self-talk keeps the nervous system in overdrive. Tomorrow brings another chance to practice kindness.",
                            calmDelta = -10, clarityDelta = -6, connectionDelta = -4, energyDelta = -10
                        )
                    )
                )
            )

            "deep_connection" -> listOf(
                SimulationStage(
                    stageNumber = 1,
                    timeOfDay = "11:20 AM • Quiet Thought",
                    title = "Thinking of Someone You Miss",
                    narrative = "$characterName thinks about a friend they haven't spoken to in weeks, but worries it's been 'too long' to reach out.",
                    choices = listOf(
                        SimulationChoice(
                            id = "dc_1a",
                            label = "Send a low-pressure voice note: 'No rush to reply, just thought of you!'",
                            outcomeText = "Removing pressure made reaching out feel effortless and warm—and your friend smiled hearing your voice.",
                            calmDelta = 10, clarityDelta = 10, connectionDelta = 18, energyDelta = 8
                        ),
                        SimulationChoice(
                            id = "dc_1b",
                            label = "Share a photo of something peaceful that reminded you of them",
                            outcomeText = "Small thoughtful gestures keep friendships alive without needing a huge schedule block.",
                            calmDelta = 8, clarityDelta = 8, connectionDelta = 15, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "dc_1c",
                            label = "Overthink the perfect message and close the app",
                            outcomeText = "Perfectionism often disguises itself as hesitation. Warmth matters far more than polished wording.",
                            calmDelta = -6, clarityDelta = -4, connectionDelta = -10, energyDelta = -4
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 2,
                    timeOfDay = "4:15 PM • Honest Conversation",
                    title = "Being Real Instead of 'I'm Fine'",
                    narrative = "Over coffee, a trusted friend genuinely asks how $characterName has been feeling lately.",
                    choices = listOf(
                        SimulationChoice(
                            id = "dc_2a",
                            label = "Share honestly: 'It's been a bit stretched lately, and I'm glad to be here'",
                            outcomeText = "Gentle honesty opened the door for your friend to share too—you both left feeling less alone.",
                            calmDelta = 12, clarityDelta = 12, connectionDelta = 18, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "dc_2b",
                            label = "Share one small win and ask what's been on their mind lately",
                            outcomeText = "Balanced curiosity created an easy, reciprocal conversation.",
                            calmDelta = 10, clarityDelta = 10, connectionDelta = 14, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "dc_2c",
                            label = "Brush it off quickly with 'All good!' and change the subject",
                            outcomeText = "Keeping armor up feels safe in the second, yet can leave you feeling unseen afterward.",
                            calmDelta = -4, clarityDelta = -4, connectionDelta = -8, energyDelta = -6
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 3,
                    timeOfDay = "7:40 PM • Gentle Social Battery",
                    title = "Ending on a Warm Note",
                    narrative = "Evening plans linger, and $characterName notices their social battery reaching 25%.",
                    choices = listOf(
                        SimulationChoice(
                            id = "dc_3a",
                            label = "Say warmly: 'I loved catching up—heading home to recharge while I'm still smiling'",
                            outcomeText = "Leaving on a high note honored both your friendship and your energy!",
                            calmDelta = 14, clarityDelta = 14, connectionDelta = 12, energyDelta = 16
                        ),
                        SimulationChoice(
                            id = "dc_3b",
                            label = "Suggest a quiet 15-minute walk together before heading separate ways",
                            outcomeText = "A soft ending gave closure without sensory overload.",
                            calmDelta = 12, clarityDelta = 8, connectionDelta = 14, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "dc_3c",
                            label = "Stay two hours past exhaustion out of fear of seeming rude",
                            outcomeText = "True friends want you rested, not depleted. Honest exits make future hangouts sweeter.",
                            calmDelta = -8, clarityDelta = -4, connectionDelta = -2, energyDelta = -14
                        )
                    )
                )
            )

            else -> listOf( // morning_rhythm
                SimulationStage(
                    stageNumber = 1,
                    timeOfDay = "7:05 AM • First Wakeful Minute",
                    title = "Before the Notifications Begin",
                    narrative = "The morning light slips through the curtains. $characterName reaches toward the bedside table where their phone is humming.",
                    choices = listOf(
                        SimulationChoice(
                            id = "mr_1a",
                            label = "Leave the phone face-down and stretch by the window for 3 slow breaths",
                            outcomeText = "Claiming the first five minutes for yourself kept your nervous system steady and unhurried.",
                            calmDelta = 16, clarityDelta = 12, connectionDelta = 4, energyDelta = 14
                        ),
                        SimulationChoice(
                            id = "mr_1b",
                            label = "Drink a full glass of water and name one intention for the day",
                            outcomeText = "A simple anchor gave your morning direction without rushing.",
                            calmDelta = 12, clarityDelta = 16, connectionDelta = 6, energyDelta = 12
                        ),
                        SimulationChoice(
                            id = "mr_1c",
                            label = "Open news headlines and work messages immediately in bed",
                            outcomeText = "Borrowed urgency flooded your mind before your feet even touched the floor.",
                            calmDelta = -12, clarityDelta = -8, connectionDelta = -4, energyDelta = -10
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 2,
                    timeOfDay = "7:40 AM • Cozy Fuel",
                    title = "An Unexpected Minor Delay",
                    narrative = "Coffee spills slightly on the counter and an email notification pops up. $characterName feels a spike of rush.",
                    choices = listOf(
                        SimulationChoice(
                            id = "mr_2a",
                            label = "Smile gently, wipe the spill slowly, and whisper 'I have enough time'",
                            outcomeText = "Meeting a tiny hiccup with humor stopped the stress spiral before it started.",
                            calmDelta = 14, clarityDelta = 12, connectionDelta = 8, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "mr_2b",
                            label = "Queue a calming 8-minute NeuroTalks reflection while getting ready",
                            outcomeText = "A warm voice in the background turned a rushed routine into a grounded ritual.",
                            calmDelta = 14, clarityDelta = 10, connectionDelta = 10, energyDelta = 12
                        ),
                        SimulationChoice(
                            id = "mr_2c",
                            label = "Skip breakfast to rush out the door 10 minutes early",
                            outcomeText = "Skipping nourishment showed up as foggy focus by mid-morning.",
                            calmDelta = -8, clarityDelta = -6, connectionDelta = -2, energyDelta = -12
                        )
                    )
                ),
                SimulationStage(
                    stageNumber = 3,
                    timeOfDay = "8:30 AM • Stepping Into the Day",
                    title = "Setting Your Compass",
                    narrative = "Ready to begin, $characterName chooses how to carry this peaceful morning rhythm forward.",
                    choices = listOf(
                        SimulationChoice(
                            id = "mr_3a",
                            label = "Choose 3 realistic 'must-dos' and leave breathing room between them",
                            outcomeText = "A humane plan creates both calm clarity and genuine follow-through.",
                            calmDelta = 12, clarityDelta = 16, connectionDelta = 6, energyDelta = 12
                        ),
                        SimulationChoice(
                            id = "mr_3b",
                            label = "Send a kind 'hope your day is gentle' note to someone you care about",
                            outcomeText = "Sharing warmth first thing amplified your own sense of connection and steadiness.",
                            calmDelta = 10, clarityDelta = 8, connectionDelta = 18, energyDelta = 10
                        ),
                        SimulationChoice(
                            id = "mr_3c",
                            label = "Pack 14 tasks into the calendar hoping to finish everything",
                            outcomeText = "Overpacked lists make even good days feel behind. Smaller lists invite steadier momentum.",
                            calmDelta = -8, clarityDelta = -8, connectionDelta = -4, energyDelta = -10
                        )
                    )
                )
            )
        }
    }

    val initialForumPosts = listOf(
        ForumPost(
            id = "post_1",
            authorAlias = "Mossy Owl",
            timestamp = "2h ago • Demo",
            category = ForumCategory.SMALL_WINS,
            moodTag = "Relieved",
            title = "I finally said 'I can't make it tonight' without inventing an excuse",
            body = "Usually I panic and write a giant paragraph explaining why I'm tired. Tonight I just texted that I needed a quiet evening to recharge, and my friend replied 'Totally get it, rest up!' Posting this here in case someone else needs permission to keep it simple.",
            warmthCount = 24,
            relateCount = 19,
            replies = listOf(
                ForumReply(
                    id = "r_101",
                    authorAlias = "Warm Chamomile",
                    timestamp = "1h ago • Demo",
                    content = "That takes real courage! Honestly, hearing friends be honest like that makes me feel safer being honest too."
                ),
                ForumReply(
                    id = "r_102",
                    authorAlias = "Morning Pine",
                    timestamp = "42m ago • Demo",
                    content = "Saving this as my gentle reminder for this weekend. Proud of you!"
                )
            )
        ),
        ForumPost(
            id = "post_2",
            authorAlias = "Quiet Fern",
            timestamp = "5h ago • Demo",
            category = ForumCategory.VENTING,
            moodTag = "Stretched Thin",
            title = "Anyone else feel weirdly tired even when the day looked 'easy' on paper?",
            body = "Nothing huge went wrong today, just lots of small decisions and background noise. By 5 PM my brain felt like a browser with 47 open tabs. Trying not to judge myself for needing a slow evening.",
            warmthCount = 31,
            relateCount = 42,
            replies = listOf(
                ForumReply(
                    id = "r_201",
                    authorAlias = "Soft Meadow",
                    timestamp = "4h ago • Demo",
                    content = "Decision fatigue is so real. Invisible mental work still uses real energy—hope you get cozy rest tonight."
                )
            )
        ),
        ForumPost(
            id = "post_3",
            authorAlias = "Morning Pine",
            timestamp = "1d ago • Demo",
            category = ForumCategory.COPING,
            moodTag = "Grounded",
            title = "The '5-minute tidy + warm tea' reset has been saving my Sundays",
            body = "When Sunday evening dread creeps in, I set a timer for just 5 minutes to clear one small surface, put on calm audio, and brew chamomile tea. It doesn't solve everything, but it shifts my headspace from overwhelmed to manageable.",
            warmthCount = 18,
            relateCount = 15,
            replies = listOf(
                ForumReply(
                    id = "r_301",
                    authorAlias = "Mossy Owl",
                    timestamp = "20h ago • Demo",
                    content = "Love how tiny and doable 5 minutes feels. Trying this tonight!"
                )
            )
        ),
        ForumPost(
            id = "post_4",
            authorAlias = "Warm Chamomile",
            timestamp = "1d ago • Demo",
            category = ForumCategory.ADVICE,
            moodTag = "Curious",
            title = "Gentle ideas for unwinding after staring at screens all day?",
            body = "By the time I finish classes and projects, my eyes and brain buzz, yet I still end up scrolling. What cozy, low-effort offline habits do you enjoy in the evening?",
            warmthCount = 14,
            relateCount = 27,
            replies = listOf(
                ForumReply(
                    id = "r_401",
                    authorAlias = "Quiet Fern",
                    timestamp = "19h ago • Demo",
                    content = "Listening to an audio reflection while stretching on the floor with dim lamps has been my favorite screen break!"
                )
            )
        )
    )

    val emergencyHotlines = listOf(
        HotlineResource(
            id = "hotline_988",
            name = "988 Suicide & Crisis Lifeline",
            region = "United States & Canada",
            numberDisplay = "988",
            dialNumber = "988",
            availability = "24/7 • Free & Confidential",
            description = "Call or text 988 to speak with a trained crisis counselor anytime you feel overwhelmed, in emotional distress, or worried about someone you love.",
            actionLabel = "Open Dialer (988)"
        ),
        HotlineResource(
            id = "hotline_text",
            name = "Crisis Text Line (Verified Standard)",
            region = "US & Canada",
            numberDisplay = "Text HOME to 741741",
            dialNumber = "741741",
            availability = "24/7 • Text Support",
            description = "Prefer typing over talking? Connect via SMS with a volunteer crisis counselor in a calm, confidential text conversation.",
            actionLabel = "Copy Number (741741)"
        ),
        HotlineResource(
            id = "hotline_emergency",
            name = "Local Emergency Services",
            region = "Immediate Physical Danger",
            numberDisplay = "911 (US) / 112 (EU) / 999 (UK)",
            dialNumber = "911",
            availability = "24/7 • Immediate Dispatch",
            description = "If you or someone with you is in immediate physical danger, please call your local emergency number or go to the nearest emergency room.",
            actionLabel = "Open Dialer (911)"
        ),
        HotlineResource(
            id = "hotline_warmline",
            name = "Peer Warm Line (Prototype Placeholder)",
            region = "Regional Demo Placeholder",
            numberDisplay = "1-800-555-0199 (Placeholder)",
            dialNumber = "18005550199",
            availability = "Demo Entry • Non-Crisis Support",
            description = "Placeholder slot for a regional non-crisis peer warm line. Replace with your verified local regional directory before production launch.",
            isPlaceholder = true,
            actionLabel = "Preview Dialer"
        ),
        HotlineResource(
            id = "hotline_intl",
            name = "International Helpline Directory",
            region = "Worldwide • findahelpline.com",
            numberDisplay = "findahelpline.com",
            dialNumber = "",
            availability = "24/7 • 130+ Countries",
            description = "Outside North America? Search free, confidential local crisis lines verified by country and language at findahelpline.com.",
            actionLabel = "Copy Website"
        )
    )

    val initialActivities = listOf(
        ActivityEntry(
            id = "act_1",
            title = "Explored NeuroTalks Prototype",
            detail = "Joined locally with anonymous alias Quiet Sprout",
            timestamp = "Just now",
            category = "Welcome"
        ),
        ActivityEntry(
            id = "act_2",
            title = "Sample Podcast Bookmarked",
            detail = "Unclenching Your Shoulders at 3 PM",
            timestamp = "Demo preset",
            category = "Podcast"
        )
    )
}
