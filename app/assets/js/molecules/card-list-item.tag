<card-list-item>
    <card title="{ title }" items="{ items }">
        <yield to="content">
            <ul class="list-group">
                <li class="list-group-item" each={ opts.items }>{ name }</li>
            </ul>
        </yield>
    </card>

    <script>
        this.title = opts.title || "Title";
        this.items = opts.items || [];
    </script>
</card-list-item>