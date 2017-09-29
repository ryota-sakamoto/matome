<card-text>
    <card title="{ title }" body="{ body }">
        <yield to="content">
            <p class="card-text">
                { opts.body }
            </p>
        </yield>
    </card>

    <script>
        this.title = opts.title || "Title";
        this.body = opts.body || "";
    </script>
</card-text>